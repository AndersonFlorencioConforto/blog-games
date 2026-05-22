CREATE TABLE threads (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  game_id VARCHAR(36) NOT NULL,
  author_id VARCHAR(36) NOT NULL,
  title VARCHAR(200) NOT NULL,
  content VARCHAR(5000) NOT NULL,
  like_count INT NOT NULL DEFAULT 0,
  reply_count INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE replies (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  thread_id VARCHAR(36) NOT NULL,
  author_id VARCHAR(36) NOT NULL,
  content VARCHAR(2000) NOT NULL,
  like_count INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE thread_likes (
  thread_id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  liked_at TIMESTAMP NOT NULL,
  PRIMARY KEY (thread_id, user_id)
);

CREATE TABLE reply_likes (
  reply_id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  liked_at TIMESTAMP NOT NULL,
  PRIMARY KEY (reply_id, user_id)
);

CREATE INDEX idx_threads_game_id ON threads(game_id);
CREATE INDEX idx_replies_thread_id ON replies(thread_id);
