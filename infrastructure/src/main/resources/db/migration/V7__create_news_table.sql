CREATE TABLE news (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  title VARCHAR(300) NOT NULL,
  summary VARCHAR(500) NOT NULL,
  content TEXT NOT NULL,
  cover_image_url VARCHAR(2048),
  author_id VARCHAR(36) NOT NULL,
  related_game_id VARCHAR(36),
  published_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_news_published_at ON news(published_at DESC);
CREATE INDEX idx_news_related_game_id ON news(related_game_id);
