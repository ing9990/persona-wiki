-- 카테고리 초기 데이터 (조건부 삽입)
INSERT INTO category (category_id, display_name, description, image_url, created_at, updated_at)
SELECT 'president',
       '대통령',
       '국가 원수 및 행정부 수장',
       'https://images.unsplash.com/photo-1555848962-6e79363ec58f?q=80&w=1000',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM category WHERE category_id = 'president');

INSERT INTO category (category_id, display_name, description, image_url, created_at, updated_at)
SELECT 'youtuber',
       '유튜버',
       '영상 콘텐츠 크리에이터',
       'https://upload.wikimedia.org/wikipedia/commons/e/ef/Youtube_logo.png',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM category WHERE category_id = 'youtuber');

INSERT INTO category (category_id, display_name, description, image_url, created_at, updated_at)
SELECT 'celebrity',
       '연예인',
       'TV, 영화, 음악 등 연예 분야 인물',
       'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?q=80&w=1000',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM category WHERE category_id = 'celebrity');

-- 대통령 카테고리 인물 데이터
-- 윤석열 대통령
INSERT INTO figure (name, image_url, biography, category_id, created_at, updated_at)
SELECT '윤석열',
       'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/South_Korea_President_Yoon_Suk_Yeol_portrait.jpg/1200px-South_Korea_President_Yoon_Suk_Yeol_portrait.jpg',
       '제20대 대한민국 대통령. 2022년 3월 대선에서 당선되어 5월 10일 취임했다. 이전에는 검찰총장을 역임했으며, 국민의힘 소속 정치인이다.',
       'president',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM figure WHERE name = '윤석열' AND category_id = 'president');

-- 박근혜 대통령
INSERT INTO figure (name, image_url, biography, category_id, created_at, updated_at)
SELECT '박근혜',
       'https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Park_Geun-hye_presidential_portrait.png/243px-Park_Geun-hye_presidential_portrait.png',
       '제18대 대한민국 대통령. 2013년 2월부터 2017년 3월까지 재임했다. 탄핵으로 인해 임기를 마치지 못했으며, 박정희 전 대통령의 딸이다.',
       'president',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM figure WHERE name = '박근혜' AND category_id = 'president');

-- 유튜버 카테고리 인물 데이터
-- 보겸
INSERT INTO figure (name, image_url, biography, category_id, created_at, updated_at)
SELECT '보겸',
       'https://i.namu.wiki/i/MWXafRhzFqdnHN-VZ561WyW55tOeYx_XjF5MgBlqfhFSkpBfESVzds2SGHDBLgdu2xy_svJRUB-kE1Z-paGgkg.webp',
       '국내 인기 유튜버. 게임 방송, 먹방, 일상 콘텐츠 등 다양한 영상을 제작한다. 2011년부터 활동을 시작하여 구독자 수 수백만 명의 인기 크리에이터로 성장했다.',
       'youtuber',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM figure WHERE name = '보겸' AND category_id = 'youtuber');

-- 냥뇽녕냥
INSERT INTO figure (name, image_url, biography, category_id, created_at, updated_at)
SELECT '냥뇽녕냥',
       'https://i.namu.wiki/i/INZjyHzdbzRCHKcT9-LakJrCXkLpvzZs4N5z-tt9ylAiPeceQuiX_Sjq6lhmNTss7VVQZ_KiOmBltA7Zw6LicQ.webp',
       '고양이와 함께하는 일상을 담은 콘텐츠를 제작하는 유튜버. 여러 마리의 고양이와 함께 생활하며 고양이의 일상과 특징, 재미있는 순간들을 담아낸다. 귀여운 영상으로 많은 반려동물 애호가들에게 인기가 있다.',
       'youtuber',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM figure WHERE name = '냥뇽녕냥' AND category_id = 'youtuber');

-- 연예인 카테고리 인물 데이터
-- BTS
INSERT INTO figure (name, image_url, biography, category_id, created_at, updated_at)
SELECT 'BTS',
       'https://upload.wikimedia.org/wikipedia/commons/0/0d/‘LG_Q7_BTS_에디션’_예약_판매_시작_%2842773472410%29_%28cropped%29.jpg'
       , '방탄소년단(BTS)은 빅히트 엔터테인먼트(현 하이브)에서 제작한 7인조 보이그룹이다. 2013년 데뷔 이후 전 세계적으로 인기를 얻으며 K-pop의 세계화에 큰 기여를 했다. 빌보드 차트 1위, 그래미 노미네이션 등 K-pop 그룹 최초의 기록들을 세웠다.',
       'celebrity',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM figure WHERE name = 'BTS' AND category_id = 'celebrity');

-- 아이즈원
INSERT INTO figure (name, image_url, biography, category_id, created_at, updated_at)
SELECT '아이즈원',
       'https://cdn2.smentertainment.com/wp-content/uploads/2025/03/에스파-첫-정규-_Armageddon_-이미지.jpg',
       '아이즈원(IZ*ONE)은 Mnet의 오디션 프로그램 \'프로듀스 48\'을 통해 결성된, 한국과 일본의 멤버로 구성된 12인조 다국적 걸그룹이다. 2018년 10월 데뷔하여 2021년 4월까지 활동했으며, 짧은 활동 기간에도 불구하고 큰 인기를 끌었다.',
       'celebrity',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM figure WHERE name = '아이즈원' AND category_id = 'celebrity');

-- 윤석열 댓글
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '경제 정책에 관심이 많아 보입니다.', 24, 12, NOW(), NOW()
FROM figure f
WHERE f.name = '윤석열'
  AND f.category_id = 'president'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '경제 정책에 관심이 많아 보입니다.');

INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '국제 관계에서 좋은 면모를 보여주고 있습니다.', 45, 8, NOW(), NOW()
FROM figure f
WHERE f.name = '윤석열'
  AND f.category_id = 'president'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '국제 관계에서 좋은 면모를 보여주고 있습니다.');

-- 박근혜 댓글
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '여성 대통령으로서 유리천장을 깼다는 점에서 의미가 있습니다.', 37, 19, NOW(), NOW()
FROM figure f
WHERE f.name = '박근혜'
  AND f.category_id = 'president'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '여성 대통령으로서 유리천장을 깼다는 점에서 의미가 있습니다.');

INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '국정 운영에 아쉬움이 많았습니다.', 48, 16, NOW(), NOW()
FROM figure f
WHERE f.name = '박근혜'
  AND f.category_id = 'president'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '국정 운영에 아쉬움이 많았습니다.');

-- 보겸 댓글
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '재미있는 콘텐츠가 많아요! 특히 게임 방송이 재밌습니다.', 89, 5, NOW(), NOW()
FROM figure f
WHERE f.name = '보겸'
  AND f.category_id = 'youtuber'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '재미있는 콘텐츠가 많아요! 특히 게임 방송이 재밌습니다.');

INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '오랜 시간 꾸준히 콘텐츠를 만들어오신 점이 대단해요.', 76, 3, NOW(), NOW()
FROM figure f
WHERE f.name = '보겸'
  AND f.category_id = 'youtuber'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '오랜 시간 꾸준히 콘텐츠를 만들어오신 점이 대단해요.');

-- 냥뇽녕냥 댓글
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '고양이들이 너무 귀여워요! 힐링됩니다.', 120, 2, NOW(), NOW()
FROM figure f
WHERE f.name = '냥뇽녕냥'
  AND f.category_id = 'youtuber'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '고양이들이 너무 귀여워요! 힐링됩니다.');

INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '고양이를 정말 사랑하시는 모습이 보기 좋아요.', 87, 1, NOW(), NOW()
FROM figure f
WHERE f.name = '냥뇽녕냥'
  AND f.category_id = 'youtuber'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '고양이를 정말 사랑하시는 모습이 보기 좋아요.');

-- BTS 댓글
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '전 세계적으로 K-pop을 알리는데 큰 역할을 했습니다!', 230, 15, NOW(), NOW()
FROM figure f
WHERE f.name = 'BTS'
  AND f.category_id = 'celebrity'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '전 세계적으로 K-pop을 알리는데 큰 역할을 했습니다!');

INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '음악성과 퍼포먼스 모두 뛰어난 아티스트입니다.', 190, 8, NOW(), NOW()
FROM figure f
WHERE f.name = 'BTS'
  AND f.category_id = 'celebrity'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '음악성과 퍼포먼스 모두 뛰어난 아티스트입니다.');

-- 아이즈원 댓글
INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '짧은 활동 기간이 너무 아쉬웠어요.', 150, 5, NOW(), NOW()
FROM figure f
WHERE f.name = '아이즈원'
  AND f.category_id = 'celebrity'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '짧은 활동 기간이 너무 아쉬웠어요.');

INSERT INTO comment (figure_id, content, likes, dislikes, created_at, updated_at)
SELECT f.figure_id, '멤버들 각자의 개성이 잘 어우러진 그룹이었어요.', 135, 3, NOW(), NOW()
FROM figure f
WHERE f.name = '아이즈원'
  AND f.category_id = 'celebrity'
  AND NOT EXISTS (SELECT 1
                  FROM comment c
                  WHERE c.figure_id = f.figure_id
                    AND c.content = '멤버들 각자의 개성이 잘 어우러진 그룹이었어요.');