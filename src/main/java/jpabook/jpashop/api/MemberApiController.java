package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

  private final MemberService memberService;

  @GetMapping("/api/v1/members")
  public List<Member> membersV1() {
    return memberService.findMembers();
  }

  @GetMapping("/api/v2/members")
  public Result membersV2() {
    List<Member> findMembers = memberService.findMembers();
    List<MemberDtO> collect = findMembers.stream().map(m -> new MemberDtO(m.getName())).collect(Collectors.toList());

    return new Result(collect);
  }

  @Data
  @AllArgsConstructor
  static class Result<T> {
    private T data;
  }

  @Data
  @AllArgsConstructor
  static class MemberDtO {
    private String name;
  }

  @PostMapping("/api/v1/members")
  public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  @PostMapping("/api/v2/members")
  public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
    Member member = new Member();
    member.setName(request.getName());

    Address address = new Address(request.city, request.street, request.zipcode);
    member.setAddress(address);


    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  @PutMapping("/api/v2/members/{id}")
  public UpdateMemberResponse updateMemberV2(
    @PathVariable("id") Long id,
    @RequestBody @Valid UpdateMemberRequest request
  ) {
    memberService.update(id, request.getName());
    Member member = memberService.findOne(id);
    return new UpdateMemberResponse(member.getId(), member.getName());
  }

  @Data
  static class CreateMemberRequest {
    @NotEmpty
    private String name;
    private String city;
    private String street;
    private String zipcode;
  }

  @Data
  static class UpdateMemberRequest {
    @NotEmpty
    private String name;
  }

  @Data
  static class CreateMemberResponse {
    private Long id;

    public CreateMemberResponse(Long id) {
      this.id = id;
    }
  }

  @Data
  @AllArgsConstructor
  static class UpdateMemberResponse {
    private Long id;
    private String name;
  }
}
