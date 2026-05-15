package com.graphql.validationconstraints.service;

import com.graphql.validationconstraints.model.AddMemberInput;
import com.graphql.validationconstraints.model.Member;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {

    private final List<Member> members = new ArrayList<>();

    public MemberService() {
        members.add(new Member("1", "Ram", "ram@gmail.com"));
        members.add(new Member("2", "Sita", "sita@gmail.com"));
    }

    public List<Member> getAllMembers() {
        return members;
    }

    public Member getMemberByEmail(String email) {
        String cleanedEmail = sanitizeEmail(email);

        return members.stream()
                .filter(member -> member.getEmail().equalsIgnoreCase(cleanedEmail))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    public Member addMember(AddMemberInput input) {
        String cleanedName = sanitizeName(input.getName());
        String cleanedEmail = sanitizeEmail(input.getEmail());

        if (cleanedName.length() < 3) {
            throw new IllegalArgumentException("Name must have at least 3 characters");
        }

        boolean exists = members.stream()
                .anyMatch(member -> member.getEmail().equalsIgnoreCase(cleanedEmail));

        if (exists) {
            throw new IllegalArgumentException("Email already exists");
        }

        Member member = new Member(
                UUID.randomUUID().toString(),
                cleanedName,
                cleanedEmail
        );

        members.add(member);
        return member;
    }

    private String sanitizeName(String name) {
        return name == null ? null : name.trim().replaceAll("\\s+", " ");
    }

    private String sanitizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}