-- ─── Database Initialization ────────────────────────────────────────────────
-- Creates separate schemas per service to enforce logical isolation
-- within a shared PostgreSQL instance (suitable for dev/test).
-- In production, each service connects to its own database instance.

-- Auth / Users
CREATE SCHEMA IF NOT EXISTS auth;

-- Orders
CREATE SCHEMA IF NOT EXISTS orders;

-- Inventory
CREATE SCHEMA IF NOT EXISTS inventory;

-- Shipments
CREATE SCHEMA IF NOT EXISTS shipments;

-- Warehouses
CREATE SCHEMA IF NOT EXISTS warehouses;

-- Notifications
CREATE SCHEMA IF NOT EXISTS notifications;

-- Analytics
CREATE SCHEMA IF NOT EXISTS analytics;

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
