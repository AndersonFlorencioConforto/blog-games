# Project instructions

## Architecture

- This repo follows a modular backend layout: domain, application, infrastructure.
- Keep framework annotations out of domain code.

## Workflow

- Prefer incremental changes over broad refactors.
- When changes span layers, implement inside-out: domain -> application -> infrastructure.

## Docs

- Architecture and ADRs live under docs/.

## Claude Code config

- Rules: .claude/rules/
- Skills: .claude/skills/
- Subagents: .claude/agents/
