# User Authentication API Documentation

## Base URL
```
http://localhost:8080
```

---

## 📋 Table of Contents
1. [Authentication Endpoints](#authentication-endpoints)
2. [Response Format](#response-format)
3. [Error Handling](#error-handling)
4. [Test Data](#test-data)
5. [Usage Examples](#usage-examples)

---

## 🔐 Authentication Endpoints

### 1. User Registration

**Endpoint:** `POST /api/auth/register`

**Description:** Create a new user account

**URL:** 
```
http://localhost:8080/api/auth/register
```

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "string (required)",
  "email": "string (required, valid email format)",
  "password": "string (required, minimum 6 characters)"
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzExNjQ2MTAwLCJleHAiOjE3MTE3MzI1MDB9.abc123..."
  },
  "timestamp": "2026-03-28T10:30:00"
}
```

**Error Response (400 Bad Request - Email exists):**
```json
{
  "success": false,
  "message": "Email already in use",
  "timestamp": "2026-03-28T10:30:00",
  "path": "/api/auth/register",
  "status": 400
}
```

**Error Response (400 Bad Request - Validation failed):**
```json
{
  "success": false,
  "message": "Validation failed",
  "timestamp": "2026-03-28T10:30:00",
  "path": "/api/auth/register",
  "status": 400,
  "errors": {
    "name": "must not be blank",
    "email": "must be a well-formed email address",
    "password": "Password must be at least 6 characters"
  }
}
```

**Test Data:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

---

### 2. User Login

**Endpoint:** `POST /api/auth/login`

**Description:** Authenticate user and receive JWT token

**URL:** 
```
http://localhost:8080/api/auth/login
```

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "string (required, valid email format)",
  "password": "string (required)"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzExNjQ2MTAwLCJleHAiOjE3MTE3MzI1MDB9.abc123..."
  },
  "timestamp": "2026-03-28T10:30:00"
}
```

**Error Response (401 Unauthorized - Invalid credentials):**
```json
{
  "success": false,
  "message": "Invalid email or password",
  "timestamp": "2026-03-28T10:30:00",
  "path": "/api/auth/login",
  "status": 401
}
```

**Error Response (401 Unauthorized - User not found):**
```json
{
  "success": false,
  "message": "User not found with email: invalid@example.com",
  "timestamp": "2026-03-28T10:30:00",
  "path": "/api/auth/login",
  "status": 401
}
```

**Test Data:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

---

### 3. OAuth2 Google Login

**Endpoint:** `GET /oauth2/authorize?client_name=google`

**Description:** Initiate Google OAuth2 authentication flow

**URL:** 
```
http://localhost:8080/oauth2/authorize?client_name=google
```

**Flow:**
1. User clicks "Login with Google"
2. Redirected to Google login page
3. User authorizes the application
4. Google redirects back to `/oauth2/callback/google`
5. User is created/updated in database
6. JWT token is generated and returned

**Redirect URI (configured in Google Console):**
```
http://localhost:8080/oauth2/callback/google
```

---

### 4. OAuth2 GitHub Login

**Endpoint:** `GET /oauth2/authorize?client_name=github`

**Description:** Initiate GitHub OAuth2 authentication flow

**URL:** 
```
http://localhost:8080/oauth2/authorize?client_name=github
```

**Flow:**
1. User clicks "Login with GitHub"
2. Redirected to GitHub login page
3. User authorizes the application
4. GitHub redirects back to `/oauth2/callback/github`
5. User is created/updated in database
6. JWT token is generated and returned

**Redirect URI (configured in GitHub OAuth App):**
```
http://localhost:8080/oauth2/callback/github
```

---

## 📤 Response Format

### Success Response Structure
```json
{
  "success": boolean,           // true for successful operations
  "message": "string",          // Human-readable message
  "data": {                     // Response data (optional)
    "token": "string"           // JWT token for authentication
  },
  "timestamp": "ISO-8601"       // When the response was generated
}
```

### Error Response Structure
```json
{
  "success": boolean,           // false for errors
  "message": "string",          // Error description
  "status": number,             // HTTP status code
  "timestamp": "ISO-8601",      // When the error occurred
  "path": "string",             // API endpoint path
  "errors": {                   // Validation errors (optional)
    "fieldName": "error message"
  }
}
```

---

## ❌ Error Handling

### HTTP Status Codes

| Status | Code | Meaning |
|--------|------|---------|
| 201 | CREATED | Registration successful, resource created |
| 200 | OK | Login successful |
| 400 | BAD_REQUEST | Validation failed, invalid input |
| 401 | UNAUTHORIZED | Invalid credentials, user not found |
| 500 | INTERNAL_SERVER_ERROR | Server error |

### Common Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| "Email already in use" | Email already registered | Use different email or login |
| "Invalid email or password" | Wrong credentials | Check email/password spelling |
| "User not found with email" | Email not registered | Register first or use correct email |
| "Validation failed" | Invalid input format | Check field values and types |
| "Password must be at least 6 characters" | Password too short | Use password with 6+ characters |
| "must be a well-formed email address" | Invalid email format | Use valid email format (user@domain.com) |

---

## 🧪 Test Data

### Valid User Registration
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

### Valid User Login
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

### Invalid Test Cases

**Missing Required Field:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
  // missing password
}
```

**Invalid Email Format:**
```json
{
  "name": "John Doe",
  "email": "invalid-email",
  "password": "SecurePassword123"
}
```

**Password Too Short:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "12345"
}
```

**Empty Fields:**
```json
{
  "name": "",
  "email": "",
  "password": ""
}
```

---

## 💡 Usage Examples

### Using cURL

#### Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "SecurePassword123"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePassword123"
  }'
```

### Using JavaScript/Fetch

#### Registration
```javascript
const registerUser = async () => {
  const response = await fetch('http://localhost:8080/api/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      name: 'John Doe',
      email: 'john.doe@example.com',
      password: 'SecurePassword123'
    })
  });
  
  const data = await response.json();
  console.log(data);
  
  if (data.success) {
    localStorage.setItem('token', data.data.token);
    console.log('Registration successful, token saved!');
  }
};
```

#### Login
```javascript
const loginUser = async () => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      email: 'john.doe@example.com',
      password: 'SecurePassword123'
    })
  });
  
  const data = await response.json();
  console.log(data);
  
  if (data.success) {
    localStorage.setItem('token', data.data.token);
    console.log('Login successful, token saved!');
  }
};
```

#### Using Token in Protected Requests
```javascript
const fetchProtectedResource = async () => {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8080/api/todos', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const data = await response.json();
  console.log(data);
};
```

### Using Postman

#### Step 1: Register User
1. Create a **POST** request
2. URL: `http://localhost:8080/api/auth/register`
3. Go to **Body** tab → Select **raw** → Select **JSON**
4. Paste:
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```
5. Click **Send**

#### Step 2: Login User
1. Create a **POST** request
2. URL: `http://localhost:8080/api/auth/login`
3. Go to **Body** tab → Select **raw** → Select **JSON**
4. Paste:
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```
5. Click **Send**
6. Copy the token from response

#### Step 3: Use Token for Protected Requests
1. Create any **GET/POST** request to a protected endpoint
2. Go to **Headers** tab
3. Add new header:
   - Key: `Authorization`
   - Value: `Bearer <your-token-here>`
4. Click **Send**

---

## 🔑 JWT Token Details

### Token Structure
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzExNjQ2MTAwLCJleHAiOjE3MTE3MzI1MDB9.abc123...
```

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "sub": "john@example.com",        // Email (subject)
  "iat": 1711646100,                // Issued at (timestamp)
  "exp": 1711732500                 // Expiration (24 hours from issue)
}
```

### Token Expiration
- **Duration:** 24 hours
- **Expiration Time:** 86400000 milliseconds from issue time
- **Action on Expiry:** Must login again to get new token

### Using Token
1. Save token after login/registration
2. Include in **Authorization** header for protected requests
3. Format: `Authorization: Bearer <token>`
4. Token is validated on each request
5. Invalid/expired tokens return 401 Unauthorized

---

## 🛡️ Security Notes

✅ **Passwords are encrypted** using BCrypt (non-reversible)  
✅ **JWT tokens are signed** with secret key  
✅ **HTTPS recommended** for production (not HTTP)  
✅ **Store tokens securely** (localStorage, sessionStorage, or cookies)  
✅ **Never expose secret key** in frontend code  
✅ **Email is unique** per account  
✅ **CORS enabled** for frontend integration  

---

## 📞 Support

For issues or questions about the authentication API, please refer to:
- [AUTHENTICATION_SETUP.md](../../AUTHENTICATION_SETUP.md) - Full setup guide
- Backend logs in `target/logs` directory

---

**Last Updated:** March 28, 2026  
**API Version:** 1.0.0  
**Status:** ✅ Active and Ready for Use
