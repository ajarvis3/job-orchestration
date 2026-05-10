# Job Lifecycle

| Start State | Event | Next State | Triggered By |
|-------------|-------|------------|--------------|
| CREATED     | queued | QUEUED | Orchestrator |
| QUEUED | consumed | RUNNING | Worker |
| RUNNING | timeout | TIMEOUT | Orchestrator |
| RUNNING | success | SUCCESS | Worker |
| RUNNING | failure | FAILURE | Worker |
| TIMEOUT | retry | CREATED | Orchestrator |
| Failure | retry | CREATED | Orchestrator |
