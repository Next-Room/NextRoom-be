# 🚪 넥스트룸

![Alt text](images/main.png?version%253D1696469455530)

> 손쉽게 관리하는 방탈출 힌트폰 매니징 서비스, 넥스트룸입니다!

## 🛠 Core Stack

- Language : `Java 17`
- Framework : `Spring Boot 3`
- DB : `MySQL 8`
- ORM : `Spring Data JPA`
- Infra : `AWS`, `NCP`

## 👨‍💻 Convention

---

<details>
<summary>
Code
</summary>
<div markdown="1">

</br>

**객체지향 생활 체조 원칙**

1. 한 메서드에 오직 한 단계의 들여쓰기만 한다.
2. else 키워드를 쓰지 않는다.
3. 모든 원시값과 문자열을 포장(wrap)한다.
4. 한 줄에 점을 하나만 찍는다.
5. 줄여쓰지 않는다.
6. 모든 entity를 작게 유지한다.
7. 2개 이상의 인스턴스 변수를 가진 클래스를 쓰지 않는다.
8. 일급 컬렉션을 쓴다.
9. getter/setter/property를 쓰지 않는다.

</br>

**코드 컨벤션**

[**네이버 핵데이 자바 코드 컨벤션**](https://naver.github.io/hackday-conventions-java/)

</div>
</details>

<details>
<summary>
Issue & PR
</summary>
<div markdown="1">
</br>

**Issue Template**
</br>

```markdown
### Issue 타입

- [ ] 기능 추가
- [ ] 기능 삭제
- [ ] 버그 수정
- [x] 코드 리팩토링

### 이슈 상세 내용

- 이슈 내용 요약 설명

### 체크리스트

- [ ] TODO1
- [ ] TODO2
```

</br>

**PR Template**

```markdown
### PR 타입

- [ ] 기능 추가
- [ ] 기능 삭제
- [ ] 버그 수정
- [x] 코드 리팩토링

### 반영 브랜치

feature/19-> develop

### 작업 사항

- 기존 username만 따로 가져가던 형태에서 관계를 매핑하여 User 객체를 통째로 참조하도록 변경
- 게시글, 댓글 모두 수정/삭제 시 username과 일치하는게 아닌 userId와 일치하는 값을 조회

### 체크리스트

- [x] 빌드에 성공했나요?
- [x] 코드 컨벤션을 잘 지켰나요? (`cmd` + `opt` + `L`)

### 테스트 결과

테스트 결과 이상 없습니다.
```

</details>

<details>
<summary>
Git Commit
</summary>
<div markdown="1">    
</br>

```markdown
# commit 내역 뒤에 이슈번호를 적어주세요!

[FEAT] 새로운 기능에 대한 커밋 (#2)

[FEAT] 새로운 기능에 대한 커밋
[FIX] 버그 수정에 대한 커밋
[BUILD] 빌드 관련 파일 수정에 대한 커밋
[CHORE] 그 외 자잘한 수정에 대한 커밋
[CI] CI 관련 설정 수정에 대한 커밋
[DOCS] 문서 수정에 대한 커밋
[STYLE] 코드 스타일 혹은 포맷 등에 관한 커밋
[REFACTOR] 코드 리팩토링에 대한 커밋
[TEST] 테스트 코드 수정에 대한 커밋
```

</details> 
    
<details>
<summary>
Branch Rule
</summary>
<div markdown="1">

</br>

![Alt text](images/branch_flow.png?version%253D1696469097435)

</br>

- PR 단위는 리뷰어가 감당할 수 있을만큼 최대한 작게 가져갑니다.
- 팀원 모두가 승인해야만 Merge 할 수 있습니다.
- 본인 PR은 본인이 Merge 합니다.
</details>

<details>
<summary>
Response
</summary>
<div markdown="1">
    
</br>

**BaseResponse**

```java
public class BaseResponseDto<T> {
    private int code;
    private String message;
    private T data;
}
```

</details> 
    
<details>
<summary>
Application Architecture
</summary>
<div markdown="1">
    
</br>

**계층형 구조**

![Alt text](images/application_architecture.png?version%253D1696469463204)

</details>
