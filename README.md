# 1. 브랜치 만들기

`git checkout -b __브랜치 이름__`

`__브랜치 이름__` 대신에 영어 초성-기능설명 으로 이름짓기

ex) `bsh-authorization-button`

# 2. 코드 내용 적용

`git add` 와 `git commit -m` 을 이용하여 수정한 파일 추가, 코드 변경내용 요약하여 기록하기

[상세 설명](https://hihiha2.tistory.com/4)

# 3. 코드 올리기

`git push`를 사용하여 코드 올리기.

올라가지 않을 경우

`git push -u origin` 적용

# 4. Pull Request 요청

## 방법 1.

`git push` 작업후 페이지에서 `Pull Request` 작업 진행

## 방법 2.

```
gh pr create --web
```

위 명령어를 통해서 코드 합치기 요청.

# 5. 완료 작업

`git checkout master` 를 이용하여 원래 브랜치로 이동

`git pull`를 이용하여 업데이트 된 내용 받기
