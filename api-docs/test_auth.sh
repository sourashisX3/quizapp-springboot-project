#!/bin/bash
# QuizApp Authentication Testing Script
# This script tests all authentication endpoints

BASE_URL="http://localhost:8083"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== QuizApp Authentication Testing ===${NC}\n"

# Test 1: Register User
echo -e "${YELLOW}Test 1: Register User${NC}"
USER_REGISTER=$(curl -s -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }')

USER_TOKEN=$(echo $USER_REGISTER | grep -o '"authToken":"[^"]*' | cut -d'"' -f4)
USER_REFRESH=$(echo $USER_REGISTER | grep -o '"refreshToken":"[^"]*' | cut -d'"' -f4)

if [ ! -z "$USER_TOKEN" ]; then
  echo -e "${GREEN}✓ User registration successful${NC}"
  echo "Access Token: ${USER_TOKEN:0:50}..."
  echo "Refresh Token: ${USER_REFRESH:0:50}..."
else
  echo -e "${RED}✗ User registration failed${NC}"
  echo $USER_REGISTER
fi

echo -e "\n${YELLOW}Test 2: Register Admin${NC}"
ADMIN_REGISTER=$(curl -s -X POST "$BASE_URL/auth/register?admin=true" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testadmin",
    "password": "adminpass123"
  }')

ADMIN_TOKEN=$(echo $ADMIN_REGISTER | grep -o '"authToken":"[^"]*' | cut -d'"' -f4)
ADMIN_REFRESH=$(echo $ADMIN_REGISTER | grep -o '"refreshToken":"[^"]*' | cut -d'"' -f4)

if [ ! -z "$ADMIN_TOKEN" ]; then
  echo -e "${GREEN}✓ Admin registration successful${NC}"
  echo "Access Token: ${ADMIN_TOKEN:0:50}..."
else
  echo -e "${RED}✗ Admin registration failed${NC}"
  echo $ADMIN_REGISTER
fi

echo -e "\n${YELLOW}Test 3: Access Protected Endpoint (USER)${NC}"
USER_QUESTIONS=$(curl -s -X GET $BASE_URL/question/all \
  -H "Authorization: Bearer $USER_TOKEN")

if echo $USER_QUESTIONS | grep -q "statusCode"; then
  echo -e "${GREEN}✓ User can access /question/all${NC}"
else
  echo -e "${RED}✗ User cannot access /question/all${NC}"
fi

echo -e "\n${YELLOW}Test 4: Try Admin-Only Endpoint (USER - Should Fail)${NC}"
USER_ADD_Q=$(curl -s -X POST $BASE_URL/question/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d '{
    "title": "Test Question",
    "description": "Description",
    "category": "Java",
    "correctAnswer": "1",
    "option1": "Option 1",
    "option2": "Option 2",
    "option3": "Option 3"
  }')

if echo $USER_ADD_Q | grep -q "403\|Access is denied"; then
  echo -e "${GREEN}✓ User correctly denied access to admin endpoint${NC}"
else
  echo -e "${YELLOW}~ Unexpected response (may need authentication check)${NC}"
fi

echo -e "\n${YELLOW}Test 5: Admin-Only Endpoint (ADMIN - Should Succeed)${NC}"
ADMIN_ADD_Q=$(curl -s -X POST $BASE_URL/question/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "title": "What is Java?",
    "description": "Java Programming Language",
    "category": "Java",
    "correctAnswer": "1",
    "option1": "Programming Language",
    "option2": "Database",
    "option3": "Framework"
  }')

if echo $ADMIN_ADD_Q | grep -q "statusCode\|created"; then
  echo -e "${GREEN}✓ Admin can add questions${NC}"
else
  echo -e "${RED}✗ Admin cannot add questions${NC}"
fi

echo -e "\n${YELLOW}Test 6: Token Refresh${NC}"
REFRESH_RESPONSE=$(curl -s -X POST $BASE_URL/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"$USER_REFRESH\"
  }")

NEW_TOKEN=$(echo $REFRESH_RESPONSE | grep -o '"authToken":"[^"]*' | cut -d'"' -f4)

if [ ! -z "$NEW_TOKEN" ]; then
  echo -e "${GREEN}✓ Token refresh successful${NC}"
  echo "New Access Token: ${NEW_TOKEN:0:50}..."
else
  echo -e "${RED}✗ Token refresh failed${NC}"
  echo $REFRESH_RESPONSE
fi

echo -e "\n${YELLOW}Test 7: Logout (Revoke Token)${NC}"
LOGOUT=$(curl -s -X POST $BASE_URL/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d "{
    \"refreshToken\": \"$USER_REFRESH\"
  }")

if echo $LOGOUT | grep -q "Logout successful"; then
  echo -e "${GREEN}✓ Logout successful${NC}"
else
  echo -e "${YELLOW}~ Logout response received${NC}"
fi

echo -e "\n${GREEN}=== Testing Complete ===${NC}\n"

