# Demo Script: Architecture Watchdog in Action

**Target Audience**: CTO, Lead Architect, Security Officers
**Duration**: 5 minutes

## Part 1: The "Happy Path" (Tenant Isolation)

**Goal**: Show that standard operations work safely within the tenant sandbox.

1. **Login** as `admin@tenant1.com`.
2. Navigate to **Users > New User**.
3. Upload a profile photo.
4. **Show File System**:
    - Open `webParent/WebBackEnd/tenants/1/assets/users/`.
    - Verify photo exists.
5. **Show Logs**:
    - Confirm no violation logs.

## Part 2: The "Attack" (Blocked Violation)

**Goal**: Demonstrate the system proactively blocking an architectural violation.

1. **Code Trigger** (Simulated via Test):
    - Run `FileUploadUtilTest.testValidatePath_ProhibitedPath()`.
    - *Alternatively, use a special "Debug" endpoint if created.*
2. **Observe**:
    - The test passes (meaning the exception WAS thrown).
    - **Console Output**: `🚨 ARCHITECTURE VIOLATION: Attempt to write to prohibited path...`

## Part 3: The Watchdog (Real-time Monitoring)

**Goal**: Show the background daemon watching the disk.

1. **Action**:
    - While the app is running, open File Explorer.
    - Manually create a folder named `site-logo` in the `WebBackEnd` root or `temp` dir watched.
2. **Observation**:
    - Watch the Application Console immediately.
    - **Alert**: `🚨 [WATCHDOG] VIOLATION: Prohibited legacy folder created...`
3. **Reporting**:
    - Open `project-reports/watchdog_status_report.html`.
    - Refresh page.
    - **Verify**: The new violation appears in the HTML table over a Red background.

## Part 4: Conclusion

1. Show `release_notes.html`.
2. Highlight the **"Secure by Default"** tag.
3. Closing Statement: *"The system now defends its own architecture at runtime. It is no longer possible for a developer
   to accidentally break tenant isolation."*
