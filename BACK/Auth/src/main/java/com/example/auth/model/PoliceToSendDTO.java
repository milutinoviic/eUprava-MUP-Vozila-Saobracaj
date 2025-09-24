package com.example.auth.model;

public class PoliceToSendDTO {
    private String id;
    private String firstName;
    private String lastName;
    private Rank rank;
    private boolean isSuspended;
    private String email;
    private String password;

    public enum Rank {
        LOW, MEDIUM, HIGH
    }

    public PoliceToSendDTO(AuthUser user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.rank = Rank.LOW;
        this.isSuspended = false;
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
}
