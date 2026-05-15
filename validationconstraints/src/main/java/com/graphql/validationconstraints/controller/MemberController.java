package com.graphql.validationconstraints.controller;
import com.graphql.validationconstraints.model.AddMemberInput;
import com.graphql.validationconstraints.model.Member;
import com.graphql.validationconstraints.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @QueryMapping
    public List<Member> members() {
        return memberService.getAllMembers();
    }

    @QueryMapping
    public Member memberByEmail(@Argument String email) {
        return memberService.getMemberByEmail(email);
    }

    @MutationMapping
    public Member addMember(@Argument @Valid AddMemberInput input) {
        return memberService.addMember(input);
    }
}
