# Authentication System Integration Guide

## 📋 Implementation Summary

Your Todo-Backend authentication system has been completed with the following components:

### ✅ Critical Files Created

1. **JwtAuthenticationFilter.java** - Security Filter
   - Validates JWT tokens from request Authorization headers
   - Extracts user info and sets authentication context
   - Location: `security/JwtAuthenticationFilter.java`

2. **CustomUserDetailsService.java** - User Details Service
   - Implements Spring Security's `UserDetailsService`
   - Loads user details by email or ID from database
   - Location: `service/CustomUserDetailsService.java`

### ✅ Exception Handling (Recommended)

3. **GlobalExceptionHandler.java** - Centralized Error Handler
   - Catches all exceptions and returns consistent error responses
   - Handles validation errors with field details
   - Location: `exception/GlobalExceptionHandler.java`

4. **ErrorResponse.java** - Error Response Model
   - Structured error response format
   - Location: `exception/ErrorResponse.java`

5. **BadRequestException.java** - Custom Exception
   - For validation and bad request errors
   - Location: `exception/BadRequestException.java`

6. **UnauthorizedException.java** - Custom Exception
   - For unauthorized access errors
   - Location: `exception/UnauthorizedException.java`

7. **ResourceNotFoundException.java** - Custom Exception
   - For resource not found errors
   - Location: `exception/ResourceNotFoundException.java`

### ✅ Utilities (Optional)

8. **ApiResponse.java** - Response Wrapper
   - Generic wrapper for all API responses
   - Ensures consistent response format across all endpoints
   - Location: `utils/ApiResponse.java`

9. **UserProfileResponse.java** - User DTO
   - Response model for user profile information
   - Location: `dto/UserProfileResponse.java`

### 🔄 Updated Files

- **AuthService.java** - Now uses `BadRequestException` instead of `RuntimeException`
- **AuthController.java** - Returns `ApiResponse<T>` wrapper for consistency

## 🔐 Authentication Flow

### 1. User Registration
```
POST /api/auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": "2026-03-28T10:30:00"
}
```

### 2. User Login
```
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": "2026-03-28T10:30:00"
}
```

### 3. Authentication with Token
```
Subsequent API Calls:
GET /api/protected-endpoint
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Flow:
1. JwtAuthenticationFilter intercepts the request
2. Extracts token from Authorization header
3. Validates token using JwtTokenProvider
4. Loads user details using CustomUserDetailsService
5. Sets authentication in SecurityContext
6. Request proceeds to controller
```

### 4. OAuth2 Login (Google/GitHub)
```
POST /oauth2/authorize?client_id=...&provider=google
-> User logs in with Google/GitHub
-> CustomOAuth2UserService handles the flow
-> User is created/updated in database
-> JWT token is generated
-> User is redirected to frontend with token
```

## 🛠️ How to Use

### Add Protected Endpoints
```java
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Todo>>> getAllTodos() {
        // Requires authentication (handled by SecurityConfig)
        return ResponseEntity.ok(ApiResponse.success("Todos retrieved", todos));
    }
}
```

### Using Exception Handlers
```java
@Service
public class TodoService {
    
    public Todo getTodo(Long id) {
        return todoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
    }
    
    public void validateTodo(TodoRequest req) {
        if (req.getTitle() == null || req.getTitle().isEmpty()) {
            throw new BadRequestException("Title cannot be empty");
        }
    }
}
```

## 🔒 Security Configuration

- **JWT Secret**: Configured in `application.properties` as `app.jwt.secret`
- **Token Expiration**: 24 hours (configurable via `app.jwt.expiration-ms`)
- **Session Management**: Stateless (using JWT)
- **CSRF Protection**: Disabled (required for JWT/API)
- **Password Encoding**: BCrypt (256 iterations)

## ⚙️ Required Configuration

Make sure `application.properties` includes:
```properties
# JWT
app.jwt.secret=YourSuperSecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmSecurity
app.jwt.expiration-ms=86400000

# Frontend URL (for OAuth2 redirect)
app.frontend-url=http://localhost:3000

# OAuth2 - Google
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=email,profile

# OAuth2 - GitHub
spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
spring.security.oauth2.client.registration.github.scope=user:email,read:user
```

## 📦 Dependencies Ready

All required dependencies are already in `pom.xml`:
- Spring Security
- Spring OAuth2 Client
- jjwt (JWT library)
- Validation
- Lombok

## 🚀 Next Steps

1. **Replace OAuth2 credentials** in `application.properties`
2. **Test the endpoints** using Postman or similar tool
3. **Add more protected endpoints** to your application
4. **Implement user profile endpoint** using `UserProfileResponse` DTO
5. **(Optional) Set up frontend** to store and send JWT tokens

## 📝 Notes

- The `JwtAuthenticationFilter` is automatically registered in `SecurityConfig`
- All endpoints under `/api/auth/**` are public (no authentication required)
- Other endpoints require valid JWT token in Authorization header
- Validation errors return 400 Bad Request with field details
- Authentication failures return 401 Unauthorized

---

**System is now ready for use!** ✨
