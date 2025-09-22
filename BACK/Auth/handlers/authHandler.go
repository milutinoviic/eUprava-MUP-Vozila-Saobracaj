package handlers

import (
	"fmt"
	"github.com/golang-jwt/jwt/v5"
	"time"
)

type TokenClaims struct {
	Email string `json:"email"`
	Role  string `json:"role"`
}

type Auth struct {
	SecretKey []byte
}

func NewAuth(secretKey []byte) *Auth {
	return &Auth{SecretKey: secretKey}
}

func (a *Auth) VerifyToken(tokenString string) (*TokenClaims, error) {
	token, err := jwt.ParseWithClaims(tokenString, &jwt.MapClaims{}, func(token *jwt.Token) (interface{}, error) {
		return a.SecretKey, nil
	})
	if err != nil {
		return nil, err
	}
	if !token.Valid {
		return nil, fmt.Errorf("invalid token")
	}

	claims, ok := token.Claims.(*jwt.MapClaims)
	if !ok {
		return nil, fmt.Errorf("unable to parse token claims")
	}

	tc := &TokenClaims{}
	if email, ok := (*claims)["email"].(string); ok {
		tc.Email = email
	}
	if role, ok := (*claims)["role"].(string); ok {
		tc.Role = role
	}
	return tc, nil
}

func (a *Auth) GenerateToken(email string, role string, ttl time.Duration) (string, time.Time, error) {
	now := time.Now().UTC()
	exp := now.Add(ttl)

	mc := jwt.MapClaims{
		"email": email,
		"role":  role,
		"iat":   now.Unix(),
		"exp":   exp.Unix(),
	}

	t := jwt.NewWithClaims(jwt.SigningMethodHS256, mc)
	signed, err := t.SignedString(a.SecretKey)
	return signed, exp, err
}
