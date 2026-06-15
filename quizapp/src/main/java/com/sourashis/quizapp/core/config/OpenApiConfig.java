package com.sourashis.quizapp.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Quiz App API")
                        .description("""
                                REST API for the Quiz Game Platform — **v1**.
                                                                
                                ---
                                                                
                                ## Default Test Credentials
                                | Role | Email | Password |
                                |------|-------|----------|
                                | Admin | admin@example.com | Admin@123 |
                                | Moderator | moderator@example.com | Moderator@123 |
                                | Player | player@example.com | Player@123 |
                                                                
                                ---
                                                                
                                ## 📦 Core Module
                                - **Authentication** — Register, login, refresh tokens, logout (`/api/v1/auth/*`)
                                - **Profile** — View / update profile, avatar, stats, password (`/api/v1/users/me`, `/api/v1/profile/*`)
                                - **Dashboard** — User, admin, moderator overviews (`/api/v1/dashboard/*`)
                                - **Friends** — Send, accept, reject, remove friends (`/api/v1/friends/*`)
                                                                
                                ### 🧭 User Registration Flow
                                1. `POST /api/v1/auth/register` — Create account (username, password, email)
                                2. `POST /api/v1/auth/login` — Login with credentials → receive JWT + refresh token
                                3. `POST /api/v1/auth/refresh` — Refresh expired access token
                                4. `GET /api/v1/users/me` — Fetch profile
                                5. `PUT /api/v1/users/me/profile` — Update display name/bio
                                6. `PATCH /api/v1/users/me/avatar` — Set profile picture
                                7. `PUT /api/v1/users/me/password` — Change password
                                > **Flow complete:** User is registered, authenticated, and has a complete profile.
                                                                
                                ---
                                                                
                                ## ❓ Quiz Engine Module
                                - **Questions** — CRUD, pagination, category filter (`/api/v1/questions/*`)
                                - **Quizzes** — Create, submit, score, categories (`/api/v1/quiz/*`)
                                - **Quiz Discovery** — Browse recent, by category, search, trending, recommended (`/api/v1/quizzes/*`)
                                - **Categories** — CRUD for quiz categories (`/api/v1/categories/*`)
                                                                
                                ### 🧭 Quiz Attempt Flow
                                1. `GET /api/v1/quizzes/recent` — Browse available quizzes
                                2. `GET /api/v1/quiz/{id}/questions` — Get quiz questions (options without correct answer)
                                3. `POST /api/v1/quiz/{id}/submit` — Submit answers (list of questionId + selectedOptionId)
                                4. System evaluates answers, calculates score, awards XP, checks achievements
                                5. Response returns score, correct/wrong count, percentage, pass status, XP earned
                                6. `GET /api/v1/users/me/stats` — View updated statistics
                                > **Flow complete:** User discovers, attempts, and gets scored on a quiz with XP awarded.
                                                                
                                ---
                                                                
                                ## 🏆 Contests & Leaderboard Module
                                - **Contests** — Create, join, participate, rank (`/api/v1/contests/*`)
                                - **Leaderboard** — Global, daily, weekly, monthly, friends (`/api/v1/leaderboard/*`)
                                                                
                                ### 🧭 Contest Participation Flow
                                1. `GET /api/v1/contests` — View active contests
                                2. `GET /api/v1/contests/{id}` — View contest details
                                3. `POST /api/v1/contests/{contestId}/join` — Join contest
                                4. `GET /api/v1/contest/{id}/questions` — Get contest questions (not shown, but submit)
                                5. `POST /api/v1/quiz/{id}/submit` — Submit answers within time limit
                                6. `GET /api/v1/contests/{contestId}/participants` — View rankings
                                7. `GET /api/v1/leaderboard/global` — Check global ranking
                                > **Flow complete:** User discovers, joins, competes, and sees their ranking.
                                                                
                                ---
                                                                
                                ## 🎖️ Gamification Module
                                - **Rewards** — Badges, achievements, claim (`/api/v1/rewards/*`)
                                - **Missions** — Daily & weekly missions with progress (`/api/v1/missions/*`)
                                - **Activity** — User activity history (`/api/v1/activity/*`)
                                                                
                                ### 🧭 Daily Engagement Flow
                                1. `GET /api/v1/missions/daily` — View daily missions
                                2. `GET /api/v1/missions/weekly` — View weekly missions
                                3. Complete a quiz → mission progress auto-updates
                                4. `POST /api/v1/missions/daily/claim/{missionId}` — Claim XP reward
                                5. `GET /api/v1/rewards/badges` — View badges and award status
                                6. `GET /api/v1/rewards/achievements` — Check achievement progress
                                7. `POST /api/v1/rewards/{rewardId}/claim` — Claim earned rewards
                                8. `GET /api/v1/activity` — View activity history
                                > **Flow complete:** User engages daily, completes missions, earns badges and achievements.
                                                                
                                ---
                                                                
                                ## 🔔 Notifications Module
                                - **Notifications** — List, unread count, mark read, preferences (`/api/v1/notifications/*`)
                                                                
                                ---
                                                                
                                ## 📁 Files Module
                                - **Files** — Upload, download, list, delete (`/api/v1/files/*`)
                                                                
                                ---
                                                                
                                ## 📊 Admin & Analytics Module
                                - **Admin Users** — Manage users, roles, status (`/api/v1/admin/users/*`)
                                - **Analytics** — User statistics (`/api/v1/analytics/*`)
                                - **Audit Logs** — Trail by user / action / resource (`/api/v1/audit/*`)
                                - **Roles** — Role & permission management (`/api/v1/roles/*`)
                                """)
                        .version("2.0.0")
                        .contact(new Contact().name("Sourashis").email("sourashis@quizapp.com"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Development server (local)"),
                        new Server().url("http://localhost:8080").description("Alternative local server"),
                        new Server().url("https://api.quizapp.com").description("Production server")
                ))
                .tags(List.of(
                        new Tag().name("Authentication").description("Register, login, refresh tokens, logout"),
                        new Tag().name("Profile").description("User profile management endpoints"),
                        new Tag().name("Dashboard").description("User, admin, and moderator dashboard endpoints"),
                        new Tag().name("Friends").description("Friend management endpoints"),
                        new Tag().name("Questions").description("Question CRUD and management endpoints"),
                        new Tag().name("Quizzes").description("Quiz creation, submission, and management endpoints"),
                        new Tag().name("Quiz Discovery").description("Browse, search, and discover quizzes"),
                        new Tag().name("Categories").description("Manage quiz categories (CRUD operations)"),
                        new Tag().name("Contests").description("Create, manage, join, and view contest endpoints"),
                        new Tag().name("Leaderboard").description("Leaderboard rankings and entries endpoints"),
                        new Tag().name("Rewards").description("Badges, achievements, and reward claiming endpoints"),
                        new Tag().name("Missions").description("Daily and weekly mission management endpoints"),
                        new Tag().name("Activity Log").description("User activity history endpoints"),
                        new Tag().name("Notifications").description("In-app notification management endpoints"),
                        new Tag().name("Files").description("File upload, retrieval, and deletion endpoints"),
                        new Tag().name("Admin Users").description("Admin user management endpoints"),
                        new Tag().name("Analytics").description("User statistics and analytics endpoints"),
                        new Tag().name("Audit Logs").description("Audit trail and activity tracking endpoints"),
                        new Tag().name("Roles").description("Role and permission management endpoints")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT-based Bearer Authentication. Obtain a token from `POST /api/v1/auth/login` or `POST /api/v1/auth/register`. Provide the token as `Bearer <token>` in the Authorization header. The JWT contains user roles and permissions used for access control.")));
    }
}
