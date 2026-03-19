# Role-Based Authentication & Refresh Token Implementation Guide

## Overview
This document covers the complete implementation of role-based access control (RBAC) with JWT authentication and refresh token functionality for the QuizApp Spring Boot application.

## What Was Implemented

### 1. **Role-Based Access Control (RBAC)**
- Two roles: `ROLE_USER` and `ROLE_ADMIN`
- Secured endpoints using `@PreAuthorize` annotations
- Question module: ADMIN can create/delete questions, any authenticated user can read
- Quiz module: ADMIN can create quizzes, any authenticated user can take/submit quizzes

### 2. **JWT Authentication with Refresh Tokens**
- Access tokens (1-hour expiry) - for API requests
- Refresh tokens (7-day expiry) - for obtaining new access tokens
- Refresh tokens stored in database and can be revoked
- Token rotation on refresh (old token revoked, new one issued)

### 3. **Security Features**
- Password encoding with BCrypt
- JWT secret key externalized to `application.properties`
- Stateless authentication with JWT
- CSRF protection disabled (suitable for stateless APIs)
- Global exception handling for auth failures

### 4. **New Entities & Repositories**
- `RefreshToken` entity with user association, expiry, and revocation status
- `RefreshTokenRepository` with finder methods for token management

### 5. **Enhanced Services**
- `AuthenticationService` - Register, login, refresh, logout
- `RefreshTokenService` - Create, validate, and manage refresh tokens
- `CustomUserDetailsService` - Load user details from database (with @Service & @Autowired fixed)
- `JwtUtil` - Generate/validate tokens, extract claims

### 6. **Exception Handling**
- `InvalidRefreshTokenException` - Custom exception for invalid/expired tokens
- Global exception handlers for:
  - `BadCredentialsException` → 401 Unauthorized
  - `JwtException` → 401 Unauthorized
  - `AuthenticationException` → 401 Unauthorized
  - Validation errors → 400 Bad Request

### 7. **Updated Endpoints**

#### Authentication Endpoints
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/auth/register` | PUBLIC | Register new user (add `?admin=true` for admin) |
| POST | `/auth/login` | PUBLIC | Login and get tokens |
| POST | `/auth/refresh` | PUBLIC | Refresh access token using refresh token |
| POST | `/auth/logout` | AUTHENTICATED | Revoke refresh token |

#### Question Module
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/question/all` | USER/ADMIN | Get all questions |
| GET | `/question/all/paged` | USER/ADMIN | Get paginated questions |
| GET | `/question/category/{name}` | USER/ADMIN | Get questions by category |
| POST | `/question/add` | ADMIN | Create new question |
| DELETE | `/question/delete/{id}` | ADMIN | Delete question |

#### Quiz Module
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/quiz/create` | ADMIN | Create new quiz |
| GET | `/quiz/{id}/questions` | USER/ADMIN | Get quiz questions |
| POST | `/quiz/{id}/submit` | USER/ADMIN | Submit quiz answers |

---

## Configuration

### application.properties
Add the following JWT configurations:

```properties
# JWT Configuration
jwt.secret=your-256-bit-secret-key-must-be-at-least-32-chars-long-replace-this-value-in-production-environment
jwt.access-token-expiry-ms=3600000
jwt.refresh-token-expiry-ms=604800000
```

**For Production:**
```bash
# Set environment variable instead
export JWT_SECRET="your-production-256-bit-secret-key-here"
```

---

## API Usage Examples

### 1. User Registration
```bash
curl -X POST http://localhost:8083/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_user",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "statusCode": 201,
  "message": "Registration successful!",
  "response": {
    "username": "john_user",
    "role": "ROLE_USER",
    "email": null,
    "phoneNumber": null,
    "address": null,
    "profilePicture": null,
    "authToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### 2. Admin Registration
```bash
curl -X POST "http://localhost:8083/auth/register?admin=true" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_user",
    "password": "admin123"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8083/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_user",
    "password": "password123"
  }'
```

**Response:** (Similar to registration, returns tokens)

### 4. Access Protected Endpoint (Get Questions)
```bash
curl -X GET http://localhost:8083/question/all \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### 5. Admin-Only Endpoint (Create Question)
```bash
curl -X POST http://localhost:8083/question/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>" \
  -d '{
    "title": "What is Spring?",
    "description": "Spring Framework Overview",
    "category": "Java",
    "correctAnswer": "1",
    "option1": "Web Framework",
    "option2": "Database",
    "option3": "IDE"
  }'
```

### 6. Refresh Access Token
```bash
curl -X POST http://localhost:8083/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<REFRESH_TOKEN>"
  }'
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Token refreshed successfully!",
  "response": {
    "username": "john_user",
    "role": "ROLE_USER",
    "authToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### 7. Logout (Revoke Refresh Token)
```bash
curl -X POST http://localhost:8083/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d '{
    "refreshToken": "<REFRESH_TOKEN>"
  }'
```

---

## Error Responses

### Invalid Credentials (401)
```json
{
  "statusCode": 401,
  "message": "Invalid username or password!"
}
```

### Invalid/Expired Refresh Token (401)
```json
{
  "statusCode": 401,
  "message": "Invalid or expired refresh token!"
}
```

### Forbidden - Insufficient Permissions (403)
```json
{
  "statusCode": 403,
  "message": "Access is denied"
}
```

### Validation Error (400)
```json
{
  "statusCode": 400,
  "message": "Validation failed: username: Username is required"
}
```

---

## Database Changes

### New Table: `refresh_tokens`
```sql
CREATE TABLE refresh_tokens (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  token VARCHAR(255) NOT NULL UNIQUE,
  expiry_date TIMESTAMP NOT NULL,
  revoked BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Updated: `users` Table
- `role` column now uses ENUM type with values: `ROLE_USER`, `ROLE_ADMIN`

---

## Security Best Practices

### 1. **Secret Key Management**
- ✅ Use strong 256-bit secret key (at least 32 characters)
- ✅ Store in environment variables, never in code
- ✅ Rotate periodically in production

### 2. **Token Management**
- ✅ Short-lived access tokens (1 hour default)
- ✅ Longer-lived refresh tokens (7 days default)
- ✅ Automatic token rotation on refresh
- ✅ Token revocation on logout

### 3. **Password Security**
- ✅ BCrypt hashing with configurable strength
- ✅ Never store plaintext passwords

### 4. **HTTPS Enforcement**
- For production: Enable HTTPS and set `Secure` flag on cookies
- Add HSTS headers

### 5. **Rate Limiting** (Future Enhancement)
- Implement rate limiting on `/auth/login` and `/auth/register`
- Prevent brute force attacks

---

## File Changes Summary

### New Files Created
- `RefreshToken.java` - Entity for storing refresh tokens
- `RefreshTokenRepository.java` - JPA Repository for RefreshToken
- `RefreshTokenService.java` - Service for managing refresh tokens
- `InvalidRefreshTokenException.java` - Custom exception for invalid tokens
- `RefreshTokenRequest.java` - DTO for refresh token endpoint

### Modified Files
- `User.java` - Changed `role` from String to `Role` enum
- `Role.java` - Fixed trailing comma syntax error
- `AuthenticationService.java` - Added refresh & logout logic
- `AuthenticationController.java` - Added `/auth/refresh` and `/auth/logout` endpoints
- `JwtUtil.java` - Externalized secret key, added refresh token methods
- `CustomUserDetailsService.java` - Added @Service & @Autowired annotations
- `SecurityConfig.java` - Updated to Spring Security 6 API with proper CSRF handling
- `GlobalExceptionHandler.java` - Added auth exception handlers
- `QuestionController.java` - Added @PreAuthorize for role-based access
- `QuizController.java` - Added @PreAuthorize for role-based access
- `application.properties` - Added JWT configuration properties
- `pom.xml` - Updated Maven compiler plugin version for Java 21 compatibility

---

## Testing the Implementation

### 1. **Start the Application**
```bash
cd E:\Projects\SpringProjects\spring\quizapp\quizapp
.\mvnw.cmd spring-boot:run
```

### 2. **Test Registration & Login**
Execute the curl commands in the "API Usage Examples" section above.

### 3. **Test Role-Based Access**
- Try accessing `/question/add` with USER role → Should return 403 Forbidden
- Try accessing `/question/add` with ADMIN role → Should succeed
- Try accessing `/question/all` with USER role → Should succeed

### 4. **Test Token Refresh**
- Extract refresh token from login response
- Call `/auth/refresh` with that token
- Verify new access token is returned
- Verify old refresh token is revoked

### 5. **Test with Expired Token**
- Wait for token to expire (or manually set short expiry for testing)
- Try accessing protected endpoint → Should return 401 Unauthorized
- Use refresh token to get new access token
- Try again with new token → Should succeed

---

## Troubleshooting

### Issue: "Invalid JWT token"
**Solution:** Ensure JWT secret in properties matches the one used during token generation. Also check token expiry.

### Issue: 403 Forbidden on admin endpoints when logged in as admin
**Solution:** Ensure role in database is exactly `ROLE_ADMIN` (case-sensitive). Check that @PreAuthorize is properly configured.

### Issue: Refresh token not working
**Solution:** 
1. Verify refresh token exists in `refresh_tokens` table
2. Check expiry date hasn't passed
3. Ensure `revoked` column is FALSE

### Issue: CORS errors
**Solution:** Add CORS configuration bean to `SecurityConfig`:
```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
        }
    };
}
```

---

## Next Steps & Enhancements

1. **Rate Limiting** - Prevent brute force attacks
2. **Token Blacklist** - Store revoked tokens to prevent usage during expiry
3. **Multi-factor Authentication** - Add 2FA for additional security
4. **OAuth2 Integration** - Support social login (Google, GitHub, etc.)
5. **Audit Logging** - Log authentication events
6. **Permission-Based Access** - Fine-grained permissions beyond just roles
7. **API Key Authentication** - For third-party integrations

---

## Support & Documentation

- Spring Security: https://spring.io/projects/spring-security
- JWT (JJWT): https://github.com/jwtk/jjwt
- Spring Boot: https://spring.io/projects/spring-boot

---

**Implementation Date:** March 20, 2026  
**Build Status:** ✅ SUCCESS  
**Compilation:** ✅ PASSED (43 source files)

