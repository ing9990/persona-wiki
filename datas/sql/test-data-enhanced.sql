-- 대통령(presfigure_idents) 카테고리 인물들 업데이트 (중립 10%대로, 숭배 또는 사형 쪽으로 치우치도록)

-- 윤석열 대통령 (사형 쪽으로 치우침 - 부정적 이미지)
UPDATE figure
SET like_count    = FLOOR(700 + RAND() * 300), -- 약 20-25%
    neutral_count = FLOOR(250 + RAND() * 150), -- 약 10-15%
    dislike_count = FLOOR(900 + RAND() * 300)  -- 약 60-70%
WHERE figure_id = 1;

-- 박근혜 대통령 (사형 쪽으로 치우침 - 부정적 이미지)
UPDATE figure
SET like_count    = FLOOR(700 + RAND() * 200), -- 약 25-30%
    neutral_count = FLOOR(250 + RAND() * 100), -- 약 10-12%
    dislike_count = FLOOR(950 + RAND() * 250)  -- 약 60-65%
WHERE figure_id = 2;

-- 문재인 대통령 (양쪽 균형 약간 숭배 우세 - 논쟁적 이미지)
UPDATE figure
SET like_count    = FLOOR(800 + RAND() * 400), -- 약 40-45%
    neutral_count = FLOOR(250 + RAND() * 150), -- 약 10-15%
    dislike_count = FLOOR(750 + RAND() * 350)  -- 약 40-45%
WHERE figure_id = 12;

-- 보겸 (숭배 쪽으로 치우침 - 긍정적 이미지)
UPDATE figure
SET like_count    = FLOOR(500 + RAND() * 200), -- 약 60-65%
    neutral_count = FLOOR(80 + RAND() * 40),   -- 약 10-15%
    dislike_count = FLOOR(200 + RAND() * 100)  -- 약 25-30%
WHERE figure_id = 3;

-- 달씨 (숭배 쪽으로 치우침 - 긍정적 이미지)
UPDATE figure
SET like_count    = FLOOR(450 + RAND() * 250), -- 약 60-70%
    neutral_count = FLOOR(70 + RAND() * 40),   -- 약 10-15%
    dislike_count = FLOOR(150 + RAND() * 100)  -- 약 20-25%
WHERE figure_id = 9;

-- 주호민 (숭배 쪽으로 치우침 - 긍정적 이미지)
UPDATE figure
SET like_count    = FLOOR(500 + RAND() * 200), -- 약 65-70%
    neutral_count = FLOOR(70 + RAND() * 40),   -- 약 10-12%
    dislike_count = FLOOR(150 + RAND() * 90)   -- 약 20-25%
WHERE figure_id = 10;

-- BTS (매우 숭배 쪽으로 치우침 - 매우 긍정적 이미지)
UPDATE figure
SET like_count    = FLOOR(250 + RAND() * 100), -- 약 70-80%
    neutral_count = FLOOR(30 + RAND() * 20),   -- 약 8-12%
    dislike_count = FLOOR(50 + RAND() * 30)    -- 약 10-15%
WHERE figure_id = 5;

-- 아이즈원 (숭배 쪽으로 치우침 - 긍정적 이미지)
UPDATE figure
SET like_count    = FLOOR(200 + RAND() * 150), -- 약 55-65%
    neutral_count = FLOOR(40 + RAND() * 20),   -- 약 10-15%
    dislike_count = FLOOR(80 + RAND() * 50)    -- 약 25-30%
WHERE figure_id = 6;

-- 카리나 (숭배 쪽으로 치우침 - 긍정적 이미지)
UPDATE figure
SET like_count    = FLOOR(220 + RAND() * 130), -- 약 60-70%
    neutral_count = FLOOR(35 + RAND() * 15),   -- 약 10-12%
    dislike_count = FLOOR(75 + RAND() * 45)    -- 약 20-25%
WHERE figure_id = 7;

-- 윈터 (숭배 쪽으로 치우침 - 긍정적 이미지)
UPDATE figure
SET like_count    = FLOOR(230 + RAND() * 120), -- 약 65-70%
    neutral_count = FLOOR(30 + RAND() * 15),   -- 약 8-12%
    dislike_count = FLOOR(70 + RAND() * 40)    -- 약 20-25%
WHERE figure_id = 11;