CREATE TABLE reports (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  reported_user_id VARCHAR(36) NOT NULL,
  reported_by_id VARCHAR(36) NOT NULL,
  reason VARCHAR(50) NOT NULL,
  description VARCHAR(1000),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  admin_note VARCHAR(2000),
  resolved_by VARCHAR(36),
  resolved_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_reported_user ON reports(reported_user_id);
CREATE INDEX idx_reports_reported_by ON reports(reported_by_id);
