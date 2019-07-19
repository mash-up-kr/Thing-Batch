CREATE TABLE category
(
  id            BIGINT NOT NULL AUTO_INCREMENT,
  category_type VARCHAR(255),
  PRIMARY KEY (id)
);


CREATE TABLE ranking
(
  id               BIGINT NOT NULL AUTO_INCREMENT,
  name             VARCHAR(255),
  ranking          BIGINT,
  create_at        DATETIME,
  view_count       BIGINT,
  subscriber_count BIGINT,
  thumbnail        VARCHAR(255),
  banner_img_url   VARCHAR(255),
  category_id      BIGINT,
  you_tuber_id     BIGINT,
  soaring          DOUBLE,
  ranking_type     VARCHAR(255),
  PRIMARY KEY (id)
);


CREATE TABLE tag
(
  id       BIGINT NOT NULL AUTO_INCREMENT,
  main_tag varchar(255),
  sub_tag  varchar(255),
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
  like_count       BIGINT,
  tag              VARCHAR(255),
  no_count         BIGINT,
  category_id      BIGINT,
  soaring          DOUBLE,
  PRIMARY KEY (id)
);

create table you_tuber_tag
(
  you_tuber_id BIGINT NOT NULL,
  tag_ids      BIGINT
);


CREATE TABLE review
(
  id                  BIGINT NOT NULL AUTO_INCREMENT,
  create_at           DATETIME,
  nick_name           VARCHAR(255),
  profile_url         VARCHAR(255),
  liked               VARCHAR(255),
  text                LONGTEXT,
  you_tuber_thumbnail VARCHAR(255),
  you_tuber_name      VARCHAR(255),
  you_tuber_id        BIGINT,
  user_id             BIGINT,
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
);

CREATE TABLE user
(
  id          BIGINT NOT NULL AUTO_INCREMENT,
  uid         VARCHAR(255),
  nick_name   VARCHAR(255),
  profile_url LONGTEXT,
  gender      VARCHAR(255),
  date_birth  INT,
  PRIMARY KEY (id)
);

CREATE TABLE search
(
  id        BIGINT NOT NULL AUTO_INCREMENT,
  user_id   BIGINT,
  create_at DATETIME,
  text      VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE check_ranking
(
  id           BIGINT NOT NULL AUTO_INCREMENT,
  num          BIGINT,
  category_id  BIGINT,
  create_at    DATETIME,
  ranking_type VARCHAR(255),

  PRIMARY KEY (id)
);

CREATE TABLE ranking_date
(
  id           BIGINT NOT NULL AUTO_INCREMENT,
  create_at    DATETIME,

  PRIMARY KEY (id)
);

