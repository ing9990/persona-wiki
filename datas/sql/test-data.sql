-- 대통령(presidents) 카테고리 인물들의 숭배, 중립, 사형 수 업데이트 (700~1200 사이)
UPDATE figure
SET like_count    = FLOOR(700 + RAND() * 501),
    neutral_count = FLOOR(700 + RAND() * 501),
    dislike_count = FLOOR(700 + RAND() * 501)
WHERE category_id = 'president';

-- 유튜버(youtubers) 카테고리 인물들의 숭배, 중립, 사형 수 업데이트 (340~700 사이)
UPDATE figure
SET like_count    = FLOOR(340 + RAND() * 361),
    neutral_count = FLOOR(340 + RAND() * 361),
    dislike_count = FLOOR(340 + RAND() * 361)
WHERE category_id = 'youtuber';

-- 연예인(celebrity) 카테고리 인물들의 숭배, 중립, 사형 수 업데이트 (150~350 사이)
UPDATE figure
SET like_count    = FLOOR(150 + RAND() * 201),
    neutral_count = FLOOR(150 + RAND() * 201),
    dislike_count = FLOOR(150 + RAND() * 201)
WHERE category_id = 'celebrity';

-- 각 인물별 댓글 추가 (숭배+중립+사형 수의 1/10 비율)
-- 산술은 데이터베이스에서 처리하도록 하여 쿼리 간소화

-- 윤석열 대통령 댓글 (50세 이상 70%, 15-25세 30%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 50세 이상 댓글 (70%)
(1, '경제 정책이 이전 정부보다 확실히 나아졌습니다. 국민의 삶이 조금씩 나아지는 것이 느껴집니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(1, '안보 문제는 확실히 강경하게 대응해야 합니다. 대통령께서 잘 하고 계십니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(1, '법치주의를 바로 세우는 것이 중요합니다. 검찰 출신의 강점이 잘 발휘되고 있습니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(1, '국가의 미래를 위해 현 정부의 정책 방향은 옳다고 생각합니다. 흔들림 없이 추진하십시오.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(1, '요즘 젊은이들은 대통령님에 대한 비판이 지나칩니다. 국가 지도자에 대한 예의가 필요합니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(1, '부동산 정책이 아직 효과를 보지 못하는 것 같습니다. 서민들의 내 집 마련 꿈이 더 어려워졌습니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(1, '어려운 시기에 국정을 운영하시느라 고생이 많으십니다. 국민들도 함께 어려움을 이겨냈으면 합니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
-- 15-25세 댓글 (30%)
(1, '대통령 지지율 떨어지는 이유가 있네요... 청년 정책은 전혀 신경 안쓰시는듯', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(1, '취업난 심각한데 정부에서는 대책이 없어 보임... 청년들은 어디로 가라는거죠?', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(1, '집값 때문에 서울 근처는 꿈도 못 꾸겠어요. 이러다 결혼도 못하고 늙겠네요 ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW());

-- 박근혜 대통령 댓글 (50세 이상 70%, 15-25세 30%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 50세 이상 댓글 (70%)
(2, '첫 여성 대통령으로서 큰 의미가 있었습니다. 안타깝게 임기를 마치지 못했지만 역사적 의의는 있습니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(2, '국정농단 사건은 매우 유감스러웠습니다. 주변 인물 관리가, 아쉬움이 많이 남습니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(2, '박정희 대통령의 딸로서 국가 안보와 경제에 대한 철학이 있었다고 생각합니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(2, '탄핵 과정이 너무 서둘러 진행된 것 같아 아쉽습니다. 법적 절차가 더 신중했으면 합니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(2, '역사는 공정하게 평가할 것입니다. 시간이 흐른 뒤 다시 생각해 볼 문제입니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(2, '국정 운영에 있어 소통이 부족했던 점은 아쉽습니다. 리더십에 약점이 있었습니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(2, '여러 아쉬움이 있지만, 재임 기간 중 외교 관계에서 성과가 있었다고 생각합니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
-- 15-25세 댓글 (30%)
(2, '정치는 잘 모르지만 역사 교과서에서 꼭 배워야 할 사례가 될 것 같아요.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(2, '국정농단 사건 때문에 정치에 관심을 갖게 됐어요. 민주주의의 중요성을 느꼈습니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(2, '현대사 수업에서 토론 주제로 자주 나왔던 인물이에요. 다양한 시각이 있더라구요.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW());

-- 문재인 대통령 댓글 (50세 이상 70%, 15-25세 30%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 50세 이상 댓글 (70%)
(12, '남북관계 개선을 위해 노력한 점은 높이 평가합니다. 평화적 공존이 중요합니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(12, '코로나19 초기 방역 대응이 국제적으로 모범 사례가 되었습니다. K-방역의 성과였습니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(12, '부동산 정책은 아쉬운 점이 많았습니다. 서민들의 주거 부담이 더 커졌습니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(12, '소득주도성장 정책의 의도는 좋았으나 현실적 결과가 아쉬웠습니다. 경제 성장의 둔화가 체감됩니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(12, '국민과의 소통을 위해 노력한 대통령이었습니다. 서민들의 목소리를 들으려 했습니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(12, '평범한 가정 출신으로 대통령이 된 점이 국민들에게 희망을 주었습니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(12, '적폐청산 과정에서 과도한 면이 있었다고 생각합니다. 국민 통합에 아쉬움이 남습니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
-- 15-25세 댓글 (30%)
(12, '촛불시위로 탄생한 대통령이라 의미가 있었어요. 시민의 힘을 보여준 역사적 순간이었죠.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(12, '첫 투표를 문재인 대통령 선거 때 했는데, 정치 참여의 중요성을 깨달았어요.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(12, '코로나 기간에 재난지원금 지급한 거 학생으로서 많이 도움됐어요! 감사했습니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW());

-- 보겸 댓글 (10-20대 85%, 기타 15%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 10-20대 댓글 (85%)
(3, '보겸님 영상 진짜 꿀잼ㅋㅋㅋ 특히 게임 방송 레전드임!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(3, '와 벌써 10년 넘게 보는 유튜버인데 아직도 재밌음ㅋㅋ 근데 예전만큼 자주 업로드 안해서 아쉽..', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(3, '보거이 목소리 진짜 ASMR 대박이다 진짜ㅠㅠ 더 자주 해주세요ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(3, '오늘 라방도 미쳤음ㅋㅋㅋ 3시간 내내 웃으면서 봄 ㄹㅇ 개꿀잼', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(3, '보겸님 덕분에 게임 시작했는데 진짜 재밌어요! 감사합니다ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(3, '보겸 리액션은 진짜 레전드... 항상 웃게 해줘서 고마워요 ㅋㅋㅋ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(3, '요즘 학교 갔다오면 무조건 보겸부터 찾아봄ㅋㅋㅋ 힐링됨 진짜', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(3, '오늘 아침에 올라온 영상 진짜 웃겨서 버스에서 민폐됐음ㅋㅋㅋㅋ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
-- 기타 연령대 댓글 (15%)
(3, '콘텐츠 제작에 늘 감사드립니다. 아이들과 함께 보고 있어요.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(3, '10년 전부터 지켜본 시청자입니다. 앞으로도 건강하게 활동 이어가시길 바랍니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW());

-- 달씨 댓글 (10-20대 85%, 기타 15%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 10-20대 댓글 (85%)
(9, '달씨 콘텐츠 너무 재밌어요ㅠㅠ 매일 기다리고 있어요!!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(9, '오늘 영상도 레전드ㅋㅋㅋ 진짜 얼마나 웃었는지 몰라요', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(9, '달씨 편집 진짜 미쳤다... 이 편집 실력 어디서 배워요? ㄹㅇ 천재 아니냐', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(9, '요즘 학교 스트레스 받는데 달씨 영상 보면 다 풀려요 감사합니다ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(9, '이번 콜라보 영상 미쳤다ㅋㅋㅋㅋ 다음에도 같이 해주세요!!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(9, '달씨 덕분에 새로운 게임 알게 됐어요! 친구들이랑 같이 하는데 진짜 재밌어요ㅎㅎ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(9, '달씨 목소리 진짜 듣기 좋음ㅠㅠ 더 자주 브이로그 해주세요!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(9, '오늘 라이브 못 봐서 너무 아쉬워요ㅠㅠ 다음에는 꼭 볼게요!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
-- 기타 연령대 댓글 (15%)
(9, '자녀와 함께 시청하고 있습니다. 건전한 콘텐츠 감사합니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(9, '콘텐츠 제작이 쉽지 않은데 항상 밝은 에너지로 영상 올려주셔서 감사합니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW());

-- 주호민 댓글 (10-20대 85%, 기타 15%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 10-20대 댓글 (85%)
(10, '주호민 작가님 만화 너무 재밌어요!! 신과함께는 진짜 인생작품..', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(10, '유튜브 시작하셔서 너무 좋아요ㅠㅠ 더 자주 올려주세요!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(10, '그림체가 너무 독특하고 좋아요! 팬아트 그려서 인스타에 올렸는데 봐주실까요..?', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(10, '요즘 디지털 드로잉 배우는 중인데 주호민 작가님 영상 많은 도움 됩니다!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(10, '이번 먹방 콘텐츠 진짜 웃겨요ㅋㅋㅋ 김치먹방 레전드!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(10, '와... 오늘 라이브 드로잉 미쳤어요 진짜... 실시간으로 그림 그리는거 보니까 너무 신기해요', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW()),
(10, '고등학생인데 작가님 보고 만화가 꿈 생겼어요! 조언 부탁드려요ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(10, '주호민 작가님 팬미팅 언제 하시나요?? 꼭 가고 싶어요!!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
-- 기타 연령대 댓글 (15%)
(10, '신과함께 시리즈 너무 감명깊게 읽었습니다. 작품활동 응원합니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(10, '만화가로서, 콘텐츠 크리에이터로서 다양한 활동 모습이 인상적입니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW());

-- BTS 댓글 (10대 후반 90%, 기타 10%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 10대 후반 댓글 (90%)
(5, '방탄 최고!!!! 전 세계가 인정한 우리 아미 자랑스러워요♥', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(5, '정국 오빠 노래 실력 미쳤다... 진짜 천재 아닌가요??', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(5, '뷔 비주얼 레전드... 이 세상 사람이 아니야 진짜로...', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(5, '지민이 춤선 너무 예뻐요ㅠㅠㅠ 진짜 천상 댄서...', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(5, '아미 3년차인데 진짜 방탄 없인 못살아요,,, 매일 스밍중!!!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(5, '새 앨범 언제 나오나요?? 너무 기대돼요 ㅠㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(5, '남준이 랩은 진짜 세계 최고... 가사 쓰는 실력도 미쳤어요', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(5, '방탄 때문에 영어 공부 시작했어요!! 덕분에 토익 900점 넘었어요!!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(5, '진짜 다음 콘서트는 무조건 가야지... 표 구하기 진짜 어렵겠지만...', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(5, '국위선양에 기여한 대표적인 그룹입니다. 한국의 문화산업 발전에 큰 역할을 했습니다.', FLOOR(1 + RAND() * 50),
 FLOOR(1 + RAND() * 30), NOW(), NOW());

-- 아이즈원 댓글 (10대 후반 90%, 기타 10%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 10대 후반 댓글 (90%)
(6, '아이즈원 영원해,,, 재결합 언제쯤 가능할까요...', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(6, '원영이 비주얼 미쳤어... 진짜 여신이다 ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(6, '라비앙로즈는 진짜 역대급 타이틀곡... 지금 들어도 소름,,', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(6, '아직도 프듀 시즌 돌려보는 사람?? 저만...?', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(6, '활동 기간이 너무 짧았어요ㅠㅠ 2년이 뭐라고...', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(6, '은비언니 솔로 데뷔했을 때 진짜 눈물났어요ㅠㅠ 응원해요!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(6, '작년 콘서트 DVD 계속 돌려보는 중... 매번 봐도 감동이에요', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(6, '짱원이 메인보컬 실력 진짜 인정...! 고음 치는거 소름,,', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(6, '위즈원 3년차!! 데뷔부터 지금까지 쭉 덕질중이에요~', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
-- 기타 연령대 댓글 (10%)
(6, '단기간에 놀라운 성과를 이룬 그룹이었습니다. 각자의 새 출발을 응원합니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW());

-- 카리나 댓글 (10대 후반 90%, 기타 10%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 10대 후반 댓글 (90%)
(7, '카리나 언니 인스타 업데이트에 심장 떨어질뻔... 너무 예뻐요ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(7, '에스파 무대 완전 압도적... 특히 카리나 파트 미쳤어요', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(7, '카리나 언니 덕분에 화장품 다 바꿨어요ㅋㅋㅋ 같은 제품 쓰고싶어서...', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(7, '오늘 음방 보고 왔는데 실물 미쳤어요... 진짜 인형같음ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(7, '카리나 언니 춤선 너무 예뻐요... 댄스커버 도전중인데 너무 어려워요ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(7, '다음 컴백은 언제인가요?? 너무 기다려져요 ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(), NOW()),
(7, '카리나 언니 연기도 너무 잘하시더라구요! 앞으로 연기활동도 기대해요~', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(7, '에스파 콘서트 티켓팅 실패했어요 ㅠㅠㅠ 너무 슬퍼요...', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(7, '요즘 카리나 언니처럼 염색하는 거 유행이던데 진짜 찰떡이에요!!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
-- 기타 연령대 댓글 (10%)
(7, '실력과 외모를 겸비한 아티스트입니다. 앞으로의 활동이 기대됩니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW());

-- 윈터 댓글 (10대 후반 90%, 기타 10%)
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
VALUES
-- 10대 후반 댓글 (90%)
(11, '윈터 언니 인스타 업로드에 매번 심장 떨려요ㅠㅠ 너무 예뻐요!!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(11, '윈터 언니 때문에 에스파 덕질 시작했어요!! 최애는 못 바꿔요ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(11, '윈터 파트 들을 때마다 소름... 목소리 진짜 독보적이에요', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(11, '오늘 새 광고 봤는데 윈터 비주얼 미쳤어... 진짜 여신이다...', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(11, '윈터 언니 화보 보고 같은 옷 샀어요ㅋㅋㅋ 비싸도 참을 수 없었어요', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(11, '윈터 언니 팬싸 갔다왔는데 실물 미쳤어요... 사진보다 100배는 예뻐요ㅠㅠ', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
(11, '에스파 컴백 언제죠? 너무 기다려져요ㅠㅠ 윈터 신곡 무대 보고싶어요', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(11, '윈터 언니 생일 서포트 준비 중인데 다들 참여해주세요!!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30), NOW(),
 NOW()),
(11, '고등학생인데 윈터 언니 때문에 아이돌 준비 시작했어요... 꿈을 줘서 고마워요!', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW()),
-- 기타 연령대 댓글 (10%)
(11, '표현력이 좋은 아티스트입니다. 앞으로 더 다양한 모습을 보여주길 기대합니다.', FLOOR(1 + RAND() * 50), FLOOR(1 + RAND() * 30),
 NOW(), NOW());