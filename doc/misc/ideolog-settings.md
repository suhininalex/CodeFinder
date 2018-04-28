# Setting up ideolog plugin for current project

Regexp for log record
```
^(\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2})\s*(\w+)\s*-\s*(\S+)\s*-\s*(.+)$
```

Available groups:
1. Time
2. Severity
3. Category
4. Message