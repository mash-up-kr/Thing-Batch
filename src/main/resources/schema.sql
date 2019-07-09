CREATE TABLE CATEGORY
(
  id            BIGINT NOT NULL AUTO_INCREMENT,
  category_type VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE YOU_TUBER
(
  id               BIGINT NOT NULL AUTO_INCREMENT,
  name             VARCHAR(255),
  description      TEXT,
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

CREATE TABLE RAKING
(
  id               BIGINT NOT NULL AUTO_INCREMENT,
  name             VARCHAR(255),
  raking           VARCHAR(255),
  create_at        DATETIME,
  view_count       BIGINT,
  subscriber_count BIGINT,
  thumbnail        VARCHAR(255),
  banner_img_url   VARCHAR(255),
  category_id      BIGINT,
  you_tuber_id     BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE CHECK_RAKING
(
  id          BIGINT NOT NULL AUTO_INCREMENT,
  num         BIGINT,
  category_id BIGINT,
  create_at   DATETIME,

  PRIMARY KEY (id)
);