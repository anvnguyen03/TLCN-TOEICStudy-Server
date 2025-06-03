# TOEIC Study Server Change Log

- [Version 1.0.0 (2024-03-19)](#version-100-2024-03-19)
  - [Added](#added-1-0-0)
  - [Changes](#changes-1-0-0)
  - [Breaking Changes](#breaking-changes-1-0-0)

## Version 1.0.0 (2024-03-19)

### Added <a id="added-1-0-0"></a>
- Implement user management APIs for admin
  - GET /api/v1/admin/user/all - Get paginated users
  - GET /api/v1/admin/user/search - Search users with filters
  - GET /api/v1/admin/user/{userId} - Get user detail with test history
  - PUT /api/v1/admin/user/update - Update user information
  - PUT /api/v1/admin/user/toggle-status - Toggle user activation status
  - DELETE /api/v1/admin/user/delete - Delete user
  - GET /api/v1/admin/user/export - Export users to Excel

### Changes <a id="changes-1-0-0"></a>
- Modify search API to handle isActivated parameter as string with "ALL" default value
- Update user deletion to check for existing test history
- Add email existence check when updating user email

### Breaking Changes <a id="breaking-changes-1-0-0"></a>
- Change isActivated parameter type in search API from Boolean to String
  - Old: `isActivated: Boolean`
  - New: `isActivated: String` (values: "ALL", "true", "false")
- Update user deletion validation
  - Old: Allow deletion of any user
  - New: Prevent deletion of users with test history