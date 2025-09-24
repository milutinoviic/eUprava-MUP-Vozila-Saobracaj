// handlers/user_handler.go
package handlers

import (
	"eUprava/Auth/repository"
	"encoding/json"
	"errors"
	"net/http"
	"strings"
	"time"
)

type UserHandler struct {
	repo *repository.UserRepository
	auth *Auth
}

func NewUserHandler(r *repository.UserRepository, secretKey []byte) *UserHandler {
	return &UserHandler{repo: r, auth: NewAuth(secretKey)}
}

func writeJSON(w http.ResponseWriter, status int, v any) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(v)
}

func badRequest(w http.ResponseWriter, msg string) {
	writeJSON(w, http.StatusBadRequest, map[string]string{"error": msg})
}

type registerReq struct {
	Email     string              `bson:"email" json:"email"`
	Password  string              `bson:"password" json:"password"`
	FirstName string              `bson:"first_name" json:"firstName"`
	LastName  string              `bson:"last_name" json:"lastName"`
	Role      repository.UserRole `bson:"role" json:"role"`
}

type authResp struct {
	Token string      `json:"token"`
	User  interface{} `json:"user"`
}

func (h *UserHandler) Register(w http.ResponseWriter, r *http.Request) {
	var req registerReq
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		badRequest(w, "invalid JSON body")
		return
	}
	req.Email = strings.TrimSpace(req.Email)
	req.FirstName = strings.TrimSpace(req.FirstName)
	req.LastName = strings.TrimSpace(req.LastName)
	req.Password = strings.TrimSpace(req.Password)

	if req.FirstName == "" || req.LastName == "" || req.Email == "" || req.Password == "" {
		badRequest(w, "fullName, email and password are required")
		return
	}
	if req.Role != repository.Admin && req.Role != repository.Police {
		badRequest(w, "invalid role")
		return
	}

	u := repository.AuthUser{
		Email:     req.Email,
		Password:  req.Password,
		FirstName: req.FirstName,
		LastName:  req.LastName,
		Role:      req.Role,
	}
	created, err := h.repo.Registration(r.Context(), u)
	if err != nil {
		if errors.Is(err, repository.ErrEmailExists) {
			writeJSON(w, http.StatusConflict, map[string]string{"error": err.Error()})
			return
		}
		writeJSON(w, http.StatusInternalServerError, map[string]string{"error": "failed to register"})
		return
	}

	token, _, err := h.auth.GenerateToken(created.Email, string(created.Role), 10*time.Minute)
	if err != nil {
		writeJSON(w, http.StatusInternalServerError, map[string]string{"error": "failed to issue token"})
		return
	}

	writeJSON(w, http.StatusCreated, authResp{
		Token: token,
		User: map[string]any{
			"id":        created.Id,
			"firstName": created.FirstName,
			"lastName":  created.LastName,
			"email":     created.Email,
			"role":      created.Role,
		},
	})
}

type loginReq struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}

func (h *UserHandler) Login(w http.ResponseWriter, r *http.Request) {
	var req loginReq
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		badRequest(w, "invalid JSON body")
		return
	}
	req.Email = strings.TrimSpace(req.Email)
	req.Password = strings.TrimSpace(req.Password)
	if req.Email == "" || req.Password == "" {
		badRequest(w, "email and password are required")
		return
	}

	u, err := h.repo.Login(r.Context(), req.Email, req.Password)
	if err != nil {
		if errors.Is(err, repository.ErrInvalidCredentials) {
			writeJSON(w, http.StatusUnauthorized, map[string]string{"error": "invalid email or password"})
			return
		}
		writeJSON(w, http.StatusInternalServerError, map[string]string{"error": "failed to login"})
		return
	}

	token, _, err := h.auth.GenerateToken(u.Email, string(u.Role), 24*time.Hour)
	if err != nil {
		writeJSON(w, http.StatusInternalServerError, map[string]string{"error": "failed to issue token"})
		return
	}

	writeJSON(w, http.StatusOK, authResp{
		Token: token,
		User: map[string]any{
			"id":        u.Id,
			"firstName": u.FirstName,
			"lastName":  u.LastName,
			"email":     u.Email,
			"role":      u.Role,
		},
	})
}

// Vraća 200 + { ok:true, email, role } ako je token validan; inače 401
func (h *UserHandler) Verify(w http.ResponseWriter, r *http.Request) {
	tokenString := parseBearerToken(r.Header.Get("Authorization"))
	if tokenString == "" {
		writeJSON(w, http.StatusUnauthorized, map[string]any{"error": "missing bearer token"})
		return
	}

	tc, err := h.auth.VerifyToken(tokenString)
	if err != nil {
		writeJSON(w, http.StatusUnauthorized, map[string]any{"error": "invalid token"})
		return
	}

	if r.Method == http.MethodHead {
		w.WriteHeader(http.StatusNoContent) // 204
		return
	}

	writeJSON(w, http.StatusOK, map[string]any{
		"ok":    true,
		"email": tc.Email,
		"role":  tc.Role,
	})
}

func parseBearerToken(header string) string {
	if header == "" {
		return ""
	}
	parts := strings.SplitN(header, " ", 2)
	if len(parts) != 2 {
		return ""
	}
	if !strings.EqualFold(parts[0], "Bearer") {
		return ""
	}
	return strings.TrimSpace(parts[1])
}
