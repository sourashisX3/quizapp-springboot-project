# RBAC Architecture — QuizApp

## Overview

Role-Based Access Control with granular permissions. The system supports **unlimited roles** and **unlimited permissions**, all stored in the database — no code changes or recompilation needed to add new ones.

```
User ──(ManyToMany)──> Role ──(ManyToMany)──> Permission
```

---

## Entity Design

### Permission (`auth/entity/Permission.java`)
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Auto-generated PK |
| name | String | Unique identifier, e.g. `question:create` |
| description | String | Human-readable description |

### Role (`auth/entity/Role.java`)
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Auto-generated PK |
| name | String | Unique, e.g. `ROLE_SUPER_ADMIN` |
| description | String | Human-readable |
| permissions | Set\<Permission\> | ManyToMany via `role_permissions` join table |

### User (`auth/entity/User.java`)
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Auto-generated PK |
| username | String | Unique, not null |
| password | String | BCrypt-encoded |
| roles | Set\<Role\> | ManyToMany via `user_roles` join table |
| email, phoneNumber, address, etc. | String | Profile info |

---

## Database Tables

| Table | Purpose |
|-------|---------|
| `users` | User accounts |
| `roles` | Role definitions |
| `permissions` | Permission definitions |
| `user_roles` | Join: user \<-> role |
| `role_permissions` | Join: role \<-> permission |

---

## Permission Catalog (22 permissions)

| Permission | Description | ROLE_USER | ROLE_ADMIN | ROLE_SUPER_ADMIN |
|-----------|-------------|:---------:|:----------:|:----------------:|
| `question:read` | View questions | ✓ | ✓ | ✓ |
| `question:create` | Create questions | | ✓ | ✓ |
| `question:update` | Update questions | | ✓ | ✓ |
| `question:delete` | Delete questions | | ✓ | ✓ |
| `quiz:read` | View quizzes | ✓ | ✓ | ✓ |
| `quiz:create` | Create quizzes | | ✓ | ✓ |
| `quiz:update` | Update quizzes | | ✓ | ✓ |
| `quiz:delete` | Delete quizzes | | ✓ | ✓ |
| `quiz:attempt` | Attempt/submit quizzes | ✓ | ✓ | ✓ |
| `category:read` | View categories | ✓ | ✓ | ✓ |
| `category:create` | Create categories | | ✓ | ✓ |
| `category:update` | Update categories | | ✓ | ✓ |
| `category:delete` | Delete categories | | ✓ | ✓ |
| `user:read` | View users | | ✓ | ✓ |
| `user:create` | Create users | | ✓ | ✓ |
| `user:update` | Update users | | ✓ | ✓ |
| `user:delete` | Delete users | | ✓ | ✓ |
| `user:manage` | Assign roles to users | | | ✓ |
| `role:read` | View roles/permissions | | ✓ | ✓ |
| `role:create` | Create new roles | | | ✓ |
| `role:update` | Update roles | | | ✓ |
| `role:delete` | Delete roles | | | ✓ |

---

## API Endpoints

### Authentication (`/auth/**`)
| Method | Path | Access | Description |
|--------|------|--------|-------------|
| POST | `/auth/register` | Permit all | Register (default: USER, pass `?admin=true` for ADMIN, pass `?role=ROLE_MODERATOR` for custom) |
| POST | `/auth/login` | Permit all | Login |
| POST | `/auth/refresh` | Permit all | Refresh token |
| POST | `/auth/logout` | Authenticated | Logout |

### Questions (`/question/**`)
| Method | Path | Required Permission |
|--------|------|-------------------|
| GET | `/question/all` | `question:read` |
| GET | `/question/all/paged` | `question:read` |
| GET | `/question/category/{name}` | `question:read` |
| GET | `/question/category-id/{id}` | `question:read` |
| POST | `/question/add` | `question:create` |
| DELETE | `/question/delete/{id}` | `question:delete` |

### Quiz (`/quiz/**`)
| Method | Path | Required Permission |
|--------|------|-------------------|
| POST | `/quiz/create` | `quiz:create` |
| GET | `/quiz/{id}/questions` | `quiz:read` |
| POST | `/quiz/{id}/submit` | `quiz:attempt` |
| GET | `/quiz/categories` | `category:read` |
| POST | `/quiz/category/add` | `category:create` |
| PUT | `/quiz/category/edit/{id}` | `category:update` |
| DELETE | `/quiz/category/delete/{id}` | `category:delete` |

### Roles Management (`/roles/**`)
| Method | Path | Required Permission | Description |
|--------|------|-------------------|-------------|
| GET | `/roles` | `role:read` | List all roles |
| GET | `/roles/{id}` | `role:read` | Get role by ID |
| POST | `/roles` | `role:create` | Create a new role |
| PUT | `/roles/{id}` | `role:update` | Update role name/description/permissions |
| DELETE | `/roles/{id}` | `role:delete` | Delete a role |
| POST | `/roles/{id}/permissions` | `role:update` | Add permissions to role |
| DELETE | `/roles/{id}/permissions` | `role:update` | Remove permissions from role |
| GET | `/roles/permissions` | `role:read` | List all permissions |

---

## Authorization Flow

1. User authenticates → JWT token contains `roles` and `permissions` claims
2. `JwtAuthenticationFilter` extracts token, loads `UserDetails` -> `CustomUserDetails.getAuthorities()` returns role names + permission names as `GrantedAuthority`
3. `@PreAuthorize("hasAuthority('quiz:create')")` checks if `quiz:create` is in the user's authorities
4. The SUPER_ADMIN role contains ALL permissions, so SUPER_ADMIN passes every `hasAuthority()` check

---

## How to Add a New Role at Runtime (No Code Changes)

```bash
# 1. Create a new role with specific permissions
POST /roles
{
  "name": "ROLE_MODERATOR",
  "description": "Can moderate content",
  "permissionNames": ["question:read", "question:update", "quiz:read"]
}

# 2. Assign that role to a user
PUT /auth/users/{userId}/roles
{
  "roleNames": ["ROLE_USER", "ROLE_MODERATOR"]
}
```

---

## How to Add a New Permission at Runtime

```bash
# 1. Create the permission (e.g., "report:read")
# (implement PermissionRequest DTO and endpoint in RolesService)

# 2. Assign it to roles via:
POST /roles/{roleId}/permissions
["report:read"]
```

---

## Default Seed Data

| Role | Default Permissions |
|------|-------------------|
| ROLE_USER | question:read, quiz:read, quiz:attempt, category:read |
| ROLE_ADMIN | All USER permissions + question:create/update/delete, quiz:create/update/delete, category:create/update/delete, user:read/create/update/delete, role:read |
| ROLE_SUPER_ADMIN | ALL 22 permissions |

**Default super admin account:** `superadmin` / `superadmin123`

---

## File Reference

| File | Purpose |
|------|---------|
| `auth/entity/Permission.java` | Permission JPA entity |
| `auth/entity/Role.java` | Role JPA entity (replaces old enum) |
| `auth/entity/User.java` | User entity with `@ManyToMany Set<Role>` |
| `auth/repository/RoleRepository.java` | Role data access |
| `auth/repository/PermissionRepository.java` | Permission data access |
| `auth/service/CustomUserDetails.java` | Authorities = roles + permissions |
| `auth/service/AuthenticationService.java` | Registration/login uses RoleRepository |
| `auth/mapper/AuthenticationMapper.java` | Maps roles set to response |
| `auth/dto/AuthenticationResponse.java` | Returns roles + permissions |
| `core/config/DataInitializer.java` | Seeds 3 roles + 22 permissions + superadmin |
| `core/config/SecurityConfig.java` | All endpoints authenticated (method-level controls) |
| `core/config/utils/JwtUtil.java` | JWT with roles + permissions claims |
| `question/controller/QuestionController.java` | Permission-based @PreAuthorize |
| `quiz/controller/QuizController.java` | Permission-based @PreAuthorize |
| `roles/controller/RolesController.java` | Role & Permission CRUD API |
| `roles/service/RolesService.java` | Role & Permission business logic |
