package repository

import (
	"context"
	"errors"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"go.opentelemetry.io/otel/codes"
	"go.opentelemetry.io/otel/trace"
	"golang.org/x/crypto/bcrypt"
	"log"
	"os"
	"time"
)

type UserRole string

const (
	Admin  UserRole = "ADMIN"
	Police UserRole = "POLICE"
)

type AuthUser struct {
	Id        string   `bson:"_id,omitempty" json:"id"`
	Email     string   `bson:"email" json:"email"`
	Password  string   `bson:"password" json:"password"`
	FirstName string   `bson:"first_name" json:"firstName"`
	LastName  string   `bson:"last_name" json:"lastName"`
	Role      UserRole `bson:"role" json:"role"`
}

var (
	ErrEmailExists        = errors.New("email already exists")
	ErrInvalidCredentials = errors.New("invalid email or password")
)

type UserRepository struct {
	cli    *mongo.Client
	logger *log.Logger
	tracer trace.Tracer
}

func (tp *UserRepository) Disconnect(ctx context.Context) error {
	_, span := tp.tracer.Start(ctx, "Disconnect")
	defer span.End()
	err := tp.cli.Disconnect(ctx)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return err
	}
	span.SetStatus(codes.Ok, "")
	return nil
}

func (tp *UserRepository) Ping() {
	ctx, span := tp.tracer.Start(context.Background(), "Ping")
	defer span.End()
	err := tp.cli.Ping(ctx, nil)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
	}
}

func NewUserRepository(logger *log.Logger, tracer trace.Tracer, ctx context.Context) (*UserRepository, error) {
	dbUri := os.Getenv("MONGO_DB_URI")
	if dbUri == "" {
		return nil, errors.New("env MONGO_DB_URI is not set")
	}
	client, err := mongo.Connect(ctx, options.Client().ApplyURI(dbUri))
	if err != nil {
		return nil, err
	}
	err = client.Ping(ctx, nil)
	if err != nil {
		return nil, err
	}
	repo := &UserRepository{
		cli:    client,
		logger: logger,
		tracer: tracer,
	}
	return repo, nil
}

func (r *UserRepository) getUserRepository() *mongo.Collection {
	projectDatabase := r.cli.Database("mongoEGovernment")
	projectCollection := projectDatabase.Collection("users")
	return projectCollection
}

func (r *UserRepository) Login(ctx context.Context, email, password string) (*AuthUser, error) {
	ctx, span := r.tracer.Start(ctx, "Login")
	defer span.End()
	var existingAccount AuthUser
	err := r.getUserRepository().FindOne(ctx, bson.M{"email": email}).Decode(&existingAccount)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, ErrInvalidCredentials
	}
	err = bcrypt.CompareHashAndPassword([]byte(existingAccount.Password), []byte(password))
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, ErrInvalidCredentials
	}
	existingAccount.Password = ""
	span.SetStatus(codes.Ok, "")
	return &existingAccount, nil
}

func EmailExists(ctx context.Context, db *mongo.Collection, email string) (bool, error) {
	ctx, cancel := context.WithTimeout(ctx, 5*time.Second)
	defer cancel()

	filter := bson.M{"email": email}
	err := db.FindOne(ctx, filter).Err()

	if errors.Is(err, mongo.ErrNoDocuments) {
		return false, nil
	}
	if err != nil {
		return false, err
	}
	return true, nil
}

func (r *UserRepository) Registration(ctx context.Context, user AuthUser) (*AuthUser, error) {
	ctx, span := r.tracer.Start(ctx, "Registration")
	defer span.End()

	exists, err := EmailExists(ctx, r.getUserRepository(), user.Email)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	if exists {
		span.RecordError(ErrEmailExists)
		return nil, ErrEmailExists
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(user.Password), bcrypt.DefaultCost)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}

	newUser := AuthUser{
		Email:     user.Email,
		Password:  string(hashedPassword),
		FirstName: user.FirstName,
		LastName:  user.LastName,
		Role:      user.Role,
	}

	result, err := r.getUserRepository().InsertOne(ctx, newUser)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}

	if oid, ok := result.InsertedID.(primitive.ObjectID); ok {
		newUser.Id = oid.Hex()
	}

	newUser.Password = ""

	return &newUser, nil
}
