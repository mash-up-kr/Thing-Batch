CREATE TABLE category
(
  id            BIGINT NOT NULL AUTO_INCREMENT,
  category_type VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE you_tuber
(
  id               BIGINT NOT NULL AUTO_INCREMENT,
  name             VARCHAR(255),
  description      LONGTEXT,
  channel_id       VARCHAR(255),
  published_at     DATETIME,
  thumbnail        VARCHAR(255),
  banner_img_url   VARCHAR(255),
  country          VARCHAR(255),
  view_count       BIGINT,
  comment_count    BIGINT,
  subscriber_count BIGINT,
  video_count      BIGINT,
  category_id      BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE ranking
(
  id               BIGINT NOT NULL AUTO_INCREMENT,
  name             VARCHAR(255),
  ranking           VARCHAR(255),
  create_at        DATETIME,
  view_count       BIGINT,
  subscriber_count BIGINT,
  thumbnail        VARCHAR(255),
  banner_img_url   VARCHAR(255),
  category_id      BIGINT,
  you_tuber_id     BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE check_raking
(
  id          BIGINT NOT NULL AUTO_INCREMENT,
  num         BIGINT,
  category_id BIGINT,
  create_at   DATETIME,

  PRIMARY KEY (id)
);

CREATE TABLE video
(
  id               BIGINT NOT NULL AUTO_INCREMENT,
  you_tuber_id     BIGINT,
  published_at     DATETIME,
  thumbnail        VARCHAR(255),
  youtube_video_id VARCHAR(255),
  title            VARCHAR(255),
  PRIMARY KEY (id)
)