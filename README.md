# README: Time Tracking Plugin
## Project Description

A time tracking plugin that automatically records the time spent on different branches in a project. 
It also allows sending updates to JIRA and generating reports in Excel format. 
This tool helps you manage your time more efficiently and automate the process of reporting progress.

<!-- Plugin description -->
This Plugin is designed to help you track your working time efficiently. It automatically monitors your work on branches,
integrates with JIRA for seamless task updates, and generates Excel reports for better time management.
<!-- Plugin description end -->

---

## Features

- **Automatic Time Tracking**:
  - The plugin automatically detects the branch you are working on and starts tracking time.
  - When switching branches, it stops the current timer and starts a new one for the new branch.

- **Session Management**:
  - Ability to manually pause and resume time tracking.
  - Edit sessions during or after completion:
    - Change branch name.
    - Change session name.
    - Add description.
    - Correct start and end times.
    - Delete session.

- **Local Data Storage**:
  - All session information is automatically saved in an XML file.

- **JIRA Integration**:
  - Ability to send session information as a comment to a JIRA task and update log time.
  - Automatically extracts the task number from the branch name (the task number must be included in the branch name).

- **Excel Reports**:
  - Generate reports and statistics of worked time in Excel format.

---

## Requirements

To use the JIRA integration feature, you need to configure the following settings in the plugin:

- **Email**: The email address associated with your JIRA account.
- **Project Key**: The key of the project to which you want to send data.
- **API Token**: An API token generated in JIRA.
- **Project URL**: The URL of the project in JIRA (e.g., `https://your-domain.atlassian.net`).

---

## Important Notes

1. **Branch Name Must Contain the JIRA Task Number**:
  - The plugin automatically attempts to extract the task number (e.g., `TASK-123`) from the branch name. 
  - Ensure that the branch name contains the correct task number for the JIRA data sending feature to work properly.

2. **Local Data Storage**:
  - All session data is stored locally in an XML file. This allows editing data even after completing work.

---

## How to Use

1. Start working on a branch – the plugin will automatically detect the branch and start tracking time.
2. At any time, you can edit session, pause or resume time tracking.
3. After completing work, edit or delete session data if necessary.
4. Send data to JIRA using the plugin's feature.
5. Generate an Excel report to summarize your work time.