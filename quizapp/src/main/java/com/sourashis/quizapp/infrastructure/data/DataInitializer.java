package com.sourashis.quizapp.infrastructure.data;

import com.sourashis.quizapp.modules.analytics.entity.UserStatistics;
import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import com.sourashis.quizapp.modules.auth.entity.Friendship;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.repository.FriendshipRepository;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import com.sourashis.quizapp.modules.contest.entity.Contest;
import com.sourashis.quizapp.modules.contest.entity.ContestParticipant;
import com.sourashis.quizapp.modules.contest.repository.ContestParticipantRepository;
import com.sourashis.quizapp.modules.contest.repository.ContestRepository;
import com.sourashis.quizapp.modules.mission.entity.DailyMission;
import com.sourashis.quizapp.modules.mission.entity.WeeklyMission;
import com.sourashis.quizapp.modules.mission.repository.DailyMissionRepository;
import com.sourashis.quizapp.modules.mission.repository.WeeklyMissionRepository;
import com.sourashis.quizapp.modules.question.entity.DifficultyLevel;
import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.entity.QuestionOption;
import com.sourashis.quizapp.modules.question.repository.QuestionOptionRepository;
import com.sourashis.quizapp.modules.question.repository.QuestionRepository;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.entity.Quiz;
import com.sourashis.quizapp.modules.quiz.entity.QuizAttempt;
import com.sourashis.quizapp.modules.quiz.entity.QuizQuestion;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizAttemptRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizQuestionRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizRepository;
import com.sourashis.quizapp.modules.reward.entity.Achievement;
import com.sourashis.quizapp.modules.reward.entity.Badge;
import com.sourashis.quizapp.modules.reward.entity.LevelConfig;
import com.sourashis.quizapp.modules.reward.repository.AchievementRepository;
import com.sourashis.quizapp.modules.reward.repository.BadgeRepository;
import com.sourashis.quizapp.modules.reward.repository.LevelConfigRepository;
import com.sourashis.quizapp.modules.roles.entity.Permission;
import com.sourashis.quizapp.modules.roles.entity.Role;
import com.sourashis.quizapp.modules.roles.repository.PermissionRepository;
import com.sourashis.quizapp.modules.roles.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private PermissionRepository permissionRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private QuestionOptionRepository questionOptionRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private QuizQuestionRepository quizQuestionRepository;
    @Autowired private QuizAttemptRepository quizAttemptRepository;
    @Autowired private UserStatisticsRepository userStatisticsRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private LevelConfigRepository levelConfigRepository;
    @Autowired private BadgeRepository badgeRepository;
    @Autowired private AchievementRepository achievementRepository;
    @Autowired private DailyMissionRepository dailyMissionRepository;
    @Autowired private WeeklyMissionRepository weeklyMissionRepository;
    @Autowired private ContestRepository contestRepository;
    @Autowired private ContestParticipantRepository contestParticipantRepository;
    @Autowired private FriendshipRepository friendshipRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Permission> permMap = seedPermissions();
        Map<String, Role> roleMap = seedRoles(permMap);
        Map<String, Category> categoryMap = seedCategories();
        Map<String, Question> questionMap = seedQuestions(categoryMap);
        seedLevelConfigs();
        seedBadges();
        seedAchievements();
        seedDailyMissions();
        seedWeeklyMissions();
        Map<String, User> userMap = seedUsers(roleMap);
        seedUserStatistics(userMap);
        seedQuizzes(categoryMap, questionMap, userMap);
        seedQuizAttempts(userMap);
        seedContests(userMap);
        seedFriendships(userMap);
        log.info("Data initialization complete.");
    }

    private Map<String, Permission> seedPermissions() {
        if (permissionRepository.count() > 0) {
            return permissionRepository.findAll().stream()
                    .collect(Collectors.toMap(Permission::getName, p -> p));
        }
        log.info("Seeding permissions...");
        Map<String, String[]> permissionDefs = new LinkedHashMap<>();
        permissionDefs.put("question:read", new String[]{"question", "read", "View questions"});
        permissionDefs.put("question:create", new String[]{"question", "create", "Create questions"});
        permissionDefs.put("question:update", new String[]{"question", "update", "Update questions"});
        permissionDefs.put("question:delete", new String[]{"question", "delete", "Delete questions"});
        permissionDefs.put("quiz:read", new String[]{"quiz", "read", "View quizzes"});
        permissionDefs.put("quiz:create", new String[]{"quiz", "create", "Create quizzes"});
        permissionDefs.put("quiz:update", new String[]{"quiz", "update", "Update quizzes"});
        permissionDefs.put("quiz:delete", new String[]{"quiz", "delete", "Delete quizzes"});
        permissionDefs.put("quiz:attempt", new String[]{"quiz", "attempt", "Attempt and submit quizzes"});
        permissionDefs.put("category:read", new String[]{"category", "read", "View categories"});
        permissionDefs.put("category:create", new String[]{"category", "create", "Create categories"});
        permissionDefs.put("category:update", new String[]{"category", "update", "Update categories"});
        permissionDefs.put("category:delete", new String[]{"category", "delete", "Delete categories"});
        permissionDefs.put("user:read", new String[]{"user", "read", "View users"});
        permissionDefs.put("user:create", new String[]{"user", "create", "Create users"});
        permissionDefs.put("user:update", new String[]{"user", "update", "Update users"});
        permissionDefs.put("user:delete", new String[]{"user", "delete", "Delete users"});
        permissionDefs.put("user:manage", new String[]{"user", "manage", "Manage user roles"});
        permissionDefs.put("role:read", new String[]{"role", "read", "View roles"});
        permissionDefs.put("role:create", new String[]{"role", "create", "Create roles"});
        permissionDefs.put("role:update", new String[]{"role", "update", "Update roles"});
        permissionDefs.put("role:delete", new String[]{"role", "delete", "Delete roles"});
        permissionDefs.put("contest:read", new String[]{"contest", "read", "View contests"});
        permissionDefs.put("contest:create", new String[]{"contest", "create", "Create contests"});
        permissionDefs.put("contest:update", new String[]{"contest", "update", "Update contests"});
        permissionDefs.put("contest:delete", new String[]{"contest", "delete", "Delete contests"});
        permissionDefs.put("contest:join", new String[]{"contest", "join", "Join contests"});
        permissionDefs.put("leaderboard:read", new String[]{"leaderboard", "read", "View leaderboard"});
        permissionDefs.put("notification:read", new String[]{"notification", "read", "View notifications"});
        permissionDefs.put("notification:send", new String[]{"notification", "send", "Send notifications"});
        permissionDefs.put("reward:read", new String[]{"reward", "read", "View rewards"});
        permissionDefs.put("reward:claim", new String[]{"reward", "claim", "Claim rewards"});
        permissionDefs.put("file:upload", new String[]{"file", "upload", "Upload files"});
        permissionDefs.put("file:delete", new String[]{"file", "delete", "Delete files"});
        permissionDefs.put("audit:read", new String[]{"audit", "read", "View audit logs"});
        permissionDefs.put("analytics:read", new String[]{"analytics", "read", "View analytics"});

        List<Permission> permissions = permissionDefs.entrySet().stream()
                .map(entry -> Permission.builder()
                        .name(entry.getKey())
                        .resource(entry.getValue()[0])
                        .action(entry.getValue()[1])
                        .description(entry.getValue()[2])
                        .build())
                .collect(Collectors.toList());

        permissionRepository.saveAll(permissions);
        log.info("Created {} permissions.", permissions.size());
        return permissions.stream().collect(Collectors.toMap(Permission::getName, p -> p));
    }

    private Map<String, Role> seedRoles(Map<String, Permission> permMap) {
        if (roleRepository.count() > 0) {
            return roleRepository.findAll().stream().collect(Collectors.toMap(Role::getName, r -> r));
        }
        log.info("Seeding roles...");

        Set<Permission> userPerms = Set.of(
                permMap.get("question:read"),
                permMap.get("quiz:read"),
                permMap.get("quiz:attempt"),
                permMap.get("category:read"),
                permMap.get("leaderboard:read"),
                permMap.get("notification:read"),
                permMap.get("reward:read"),
                permMap.get("reward:claim"),
                permMap.get("file:upload"),
                permMap.get("contest:read"),
                permMap.get("contest:join")
        );

        Set<Permission> adminPerms = Set.of(
                permMap.get("question:read"), permMap.get("question:create"), permMap.get("question:update"), permMap.get("question:delete"),
                permMap.get("quiz:read"), permMap.get("quiz:create"), permMap.get("quiz:update"), permMap.get("quiz:delete"), permMap.get("quiz:attempt"),
                permMap.get("category:read"), permMap.get("category:create"), permMap.get("category:update"), permMap.get("category:delete"),
                permMap.get("user:read"), permMap.get("user:create"), permMap.get("user:update"), permMap.get("user:delete"),
                permMap.get("role:read"),
                permMap.get("leaderboard:read"),
                permMap.get("notification:read"), permMap.get("notification:send"),
                permMap.get("reward:read"), permMap.get("reward:claim"),
                permMap.get("file:upload"), permMap.get("file:delete"),
                permMap.get("contest:read"), permMap.get("contest:join"),
                permMap.get("audit:read"),
                permMap.get("analytics:read")
        );

        Set<Permission> superAdminPerms = Set.copyOf(permMap.values());

        Role userRole = Role.builder().name("ROLE_USER").description("Regular user with basic permissions").isSystem(true).permissions(userPerms).build();
        Role adminRole = Role.builder().name("ROLE_ADMIN").description("Administrator with elevated permissions").isSystem(true).permissions(adminPerms).build();
        Role superAdminRole = Role.builder().name("ROLE_SUPER_ADMIN").description("Super Administrator with all permissions").isSystem(true).permissions(superAdminPerms).build();

        roleRepository.saveAll(List.of(userRole, adminRole, superAdminRole));
        log.info("Created 3 roles: ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN");
        return Map.of("ROLE_USER", userRole, "ROLE_ADMIN", adminRole, "ROLE_SUPER_ADMIN", superAdminRole);
    }

    private Map<String, Category> seedCategories() {
        if (categoryRepository.count() > 0) {
            return categoryRepository.findAll().stream().collect(Collectors.toMap(Category::getName, c -> c));
        }
        log.info("Seeding categories...");
        Category programming = Category.builder().name("Programming").description("Coding, development, and software engineering").iconUrl("").build();
        Category science = Category.builder().name("Science").description("Physics, chemistry, biology, and more").iconUrl("").build();
        Category mathematics = Category.builder().name("Mathematics").description("Numbers, formulas, and problem solving").iconUrl("").build();
        Category generalKnowledge = Category.builder().name("General Knowledge").description("World facts, history, geography, and culture").iconUrl("").build();

        categoryRepository.saveAll(List.of(programming, science, mathematics, generalKnowledge));
        log.info("Created 4 categories: Programming, Science, Mathematics, General Knowledge");
        return Map.of("Programming", programming, "Science", science, "Mathematics", mathematics, "General Knowledge", generalKnowledge);
    }

    private Map<String, Question> seedQuestions(Map<String, Category> categoryMap) {
        if (questionRepository.count() > 0) {
            return questionRepository.findAll().stream().collect(Collectors.toMap(Question::getTitle, q -> q, (a, b) -> a));
        }
        log.info("Seeding questions...");

        Category programming = categoryMap.get("Programming");
        Category science = categoryMap.get("Science");
        Category mathematics = categoryMap.get("Mathematics");
        Category generalKnowledge = categoryMap.get("General Knowledge");

        Question q1 = questionRepository.save(Question.builder()
                .title("What is the correct way to declare a variable in Java?")
                .category(programming).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q1).optionText("int x = 5;").isCorrect(true).sortOrder(1).build(),
                QuestionOption.builder().question(q1).optionText("variable x = 5;").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q1).optionText("x = 5 int;").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q1).optionText("int x == 5;").isCorrect(false).sortOrder(4).build()
        ));

        Question q2 = questionRepository.save(Question.builder()
                .title("Which of these is not an OOP principle?")
                .category(programming).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q2).optionText("Encapsulation").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q2).optionText("Inheritance").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q2).optionText("Polymorphism").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q2).optionText("Compilation").isCorrect(true).sortOrder(4).build()
        ));

        Question q3 = questionRepository.save(Question.builder()
                .title("What is the chemical symbol for water?")
                .category(science).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q3).optionText("H2O").isCorrect(true).sortOrder(1).build(),
                QuestionOption.builder().question(q3).optionText("CO2").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q3).optionText("NaCl").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q3).optionText("O2").isCorrect(false).sortOrder(4).build()
        ));

        Question q4 = questionRepository.save(Question.builder()
                .title("What planet is known as the Red Planet?")
                .category(science).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q4).optionText("Venus").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q4).optionText("Mars").isCorrect(true).sortOrder(2).build(),
                QuestionOption.builder().question(q4).optionText("Jupiter").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q4).optionText("Saturn").isCorrect(false).sortOrder(4).build()
        ));

        Question q5 = questionRepository.save(Question.builder()
                .title("What is the value of Pi (\u03c0) to 2 decimal places?")
                .category(mathematics).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q5).optionText("3.14").isCorrect(true).sortOrder(1).build(),
                QuestionOption.builder().question(q5).optionText("3.16").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q5).optionText("3.12").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q5).optionText("3.18").isCorrect(false).sortOrder(4).build()
        ));

        Question q6 = questionRepository.save(Question.builder()
                .title("What is the square root of 144?")
                .category(mathematics).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q6).optionText("10").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q6).optionText("12").isCorrect(true).sortOrder(2).build(),
                QuestionOption.builder().question(q6).optionText("14").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q6).optionText("16").isCorrect(false).sortOrder(4).build()
        ));

        Question q7 = questionRepository.save(Question.builder()
                .title("Which country has the largest population?")
                .category(generalKnowledge).difficulty(DifficultyLevel.MEDIUM).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q7).optionText("India").isCorrect(true).sortOrder(1).build(),
                QuestionOption.builder().question(q7).optionText("USA").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q7).optionText("Indonesia").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q7).optionText("China").isCorrect(false).sortOrder(4).build()
        ));

        Question q8 = questionRepository.save(Question.builder()
                .title("What is the capital of Japan?")
                .category(generalKnowledge).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q8).optionText("Seoul").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q8).optionText("Beijing").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q8).optionText("Tokyo").isCorrect(true).sortOrder(3).build(),
                QuestionOption.builder().question(q8).optionText("Bangkok").isCorrect(false).sortOrder(4).build()
        ));

        // Additional questions for richer test data
        Question q9 = questionRepository.save(Question.builder()
                .title("What is the time complexity of binary search?")
                .category(programming).difficulty(DifficultyLevel.MEDIUM).points(15).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q9).optionText("O(n)").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q9).optionText("O(log n)").isCorrect(true).sortOrder(2).build(),
                QuestionOption.builder().question(q9).optionText("O(n log n)").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q9).optionText("O(n^2)").isCorrect(false).sortOrder(4).build()
        ));

        Question q10 = questionRepository.save(Question.builder()
                .title("Which design pattern ensures a class has only one instance?")
                .category(programming).difficulty(DifficultyLevel.HARD).points(20).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q10).optionText("Factory Pattern").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q10).optionText("Observer Pattern").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q10).optionText("Singleton Pattern").isCorrect(true).sortOrder(3).build(),
                QuestionOption.builder().question(q10).optionText("Builder Pattern").isCorrect(false).sortOrder(4).build()
        ));

        Question q11 = questionRepository.save(Question.builder()
                .title("What is the speed of light in vacuum (approx)?")
                .category(science).difficulty(DifficultyLevel.MEDIUM).points(15).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q11).optionText("3 \u00d7 10^6 m/s").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q11).optionText("3 \u00d7 10^8 m/s").isCorrect(true).sortOrder(2).build(),
                QuestionOption.builder().question(q11).optionText("3 \u00d7 10^10 m/s").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q11).optionText("3 \u00d7 10^4 m/s").isCorrect(false).sortOrder(4).build()
        ));

        Question q12 = questionRepository.save(Question.builder()
                .title("What is the atomic number of Carbon?")
                .category(science).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q12).optionText("4").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q12).optionText("8").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q12).optionText("6").isCorrect(true).sortOrder(3).build(),
                QuestionOption.builder().question(q12).optionText("12").isCorrect(false).sortOrder(4).build()
        ));

        Question q13 = questionRepository.save(Question.builder()
                .title("What is the derivative of x\u00b2?")
                .category(mathematics).difficulty(DifficultyLevel.MEDIUM).points(15).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q13).optionText("x").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q13).optionText("2x").isCorrect(true).sortOrder(2).build(),
                QuestionOption.builder().question(q13).optionText("x\u00b2").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q13).optionText("2").isCorrect(false).sortOrder(4).build()
        ));

        Question q14 = questionRepository.save(Question.builder()
                .title("Who wrote Romeo and Juliet?")
                .category(generalKnowledge).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q14).optionText("Charles Dickens").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q14).optionText("William Shakespeare").isCorrect(true).sortOrder(2).build(),
                QuestionOption.builder().question(q14).optionText("Jane Austen").isCorrect(false).sortOrder(3).build(),
                QuestionOption.builder().question(q14).optionText("Mark Twain").isCorrect(false).sortOrder(4).build()
        ));

        Question q15 = questionRepository.save(Question.builder()
                .title("What gas do plants primarily absorb from the atmosphere?")
                .category(science).difficulty(DifficultyLevel.EASY).points(10).build());
        questionOptionRepository.saveAll(List.of(
                QuestionOption.builder().question(q15).optionText("Oxygen").isCorrect(false).sortOrder(1).build(),
                QuestionOption.builder().question(q15).optionText("Nitrogen").isCorrect(false).sortOrder(2).build(),
                QuestionOption.builder().question(q15).optionText("Carbon Dioxide").isCorrect(true).sortOrder(3).build(),
                QuestionOption.builder().question(q15).optionText("Hydrogen").isCorrect(false).sortOrder(4).build()
        ));

        log.info("Created 15 questions with options.");
        return questionRepository.findAll().stream().collect(Collectors.toMap(Question::getTitle, q -> q, (a, b) -> a));
    }

    private void seedLevelConfigs() {
        if (levelConfigRepository.count() > 0) return;
        log.info("Seeding level config...");
        levelConfigRepository.saveAll(List.of(
                LevelConfig.builder().level(1).xpRequired(0L).title("Beginner").build(),
                LevelConfig.builder().level(2).xpRequired(100L).title("Novice").build(),
                LevelConfig.builder().level(3).xpRequired(250L).title("Apprentice").build(),
                LevelConfig.builder().level(4).xpRequired(500L).title("Scholar").build(),
                LevelConfig.builder().level(5).xpRequired(1000L).title("Expert").build(),
                LevelConfig.builder().level(6).xpRequired(2000L).title("Master").build(),
                LevelConfig.builder().level(7).xpRequired(3500L).title("Grandmaster").build(),
                LevelConfig.builder().level(8).xpRequired(5000L).title("Sage").build(),
                LevelConfig.builder().level(9).xpRequired(7500L).title("Legend").build(),
                LevelConfig.builder().level(10).xpRequired(10000L).title("Mythic").build()
        ));
        log.info("Created 10 level configs.");
    }

    private void seedBadges() {
        if (badgeRepository.count() > 0) return;
        log.info("Seeding badges...");
        badgeRepository.saveAll(List.of(
                Badge.builder().name("Quick Starter").description("Complete 1 quiz").badgeType("QUIZZES_TAKEN").criteriaJson("{\"type\":\"QUIZZES_TAKEN\",\"operator\":\">=\",\"value\":1}").pointsReward(0).build(),
                Badge.builder().name("Quiz Enthusiast").description("Complete 10 quizzes").badgeType("QUIZZES_TAKEN").criteriaJson("{\"type\":\"QUIZZES_TAKEN\",\"operator\":\">=\",\"value\":10}").pointsReward(0).build(),
                Badge.builder().name("Quiz Master").description("Complete 50 quizzes").badgeType("QUIZZES_TAKEN").criteriaJson("{\"type\":\"QUIZZES_TAKEN\",\"operator\":\">=\",\"value\":50}").pointsReward(0).build(),
                Badge.builder().name("Perfect Score").description("Get 100% on a quiz").badgeType("ACCURACY").criteriaJson("{\"type\":\"ACCURACY\",\"operator\":\">=\",\"value\":100}").pointsReward(0).build(),
                Badge.builder().name("Streak Master").description("7-day streak").badgeType("STREAK").criteriaJson("{\"type\":\"STREAK\",\"operator\":\">=\",\"value\":7}").pointsReward(0).build(),
                Badge.builder().name("Iron Will").description("30-day streak").badgeType("STREAK").criteriaJson("{\"type\":\"STREAK\",\"operator\":\">=\",\"value\":30}").pointsReward(0).build(),
                Badge.builder().name("XP Hunter").description("Earn 1000 XP").badgeType("TOTAL_XP").criteriaJson("{\"type\":\"TOTAL_XP\",\"operator\":\">=\",\"value\":1000}").pointsReward(0).build(),
                Badge.builder().name("XP Legend").description("Earn 10000 XP").badgeType("TOTAL_XP").criteriaJson("{\"type\":\"TOTAL_XP\",\"operator\":\">=\",\"value\":10000}").pointsReward(0).build(),
                Badge.builder().name("Champion").description("Win 5 contests").badgeType("CONTESTS_WON").criteriaJson("{\"type\":\"CONTESTS_WON\",\"operator\":\">=\",\"value\":5}").pointsReward(0).build(),
                Badge.builder().name("Scholar").description("Pass 25 quizzes").badgeType("QUIZZES_PASSED").criteriaJson("{\"type\":\"QUIZZES_PASSED\",\"operator\":\">=\",\"value\":25}").pointsReward(0).build()
        ));
        log.info("Created 10 badges.");
    }

    private void seedAchievements() {
        if (achievementRepository.count() > 0) return;
        log.info("Seeding achievements...");
        achievementRepository.saveAll(List.of(
                Achievement.builder().name("First Steps").description("Complete your first quiz").criteriaType("QUIZZES_TAKEN").criteriaValue(1).xpReward(50).build(),
                Achievement.builder().name("Getting Started").description("Complete 5 quizzes").criteriaType("QUIZZES_TAKEN").criteriaValue(5).xpReward(100).build(),
                Achievement.builder().name("Dedicated").description("Complete 25 quizzes").criteriaType("QUIZZES_TAKEN").criteriaValue(25).xpReward(250).build(),
                Achievement.builder().name("Obsessed").description("Complete 100 quizzes").criteriaType("QUIZZES_TAKEN").criteriaValue(100).xpReward(500).build(),
                Achievement.builder().name("Perfect Streak").description("Reach 7-day streak").criteriaType("STREAK").criteriaValue(7).xpReward(200).build(),
                Achievement.builder().name("Unstoppable").description("Reach 30-day streak").criteriaType("STREAK").criteriaValue(30).xpReward(1000).build(),
                Achievement.builder().name("Knowledge Seeker").description("Get 100 correct answers").criteriaType("CORRECT_ANSWERS").criteriaValue(100).xpReward(150).build(),
                Achievement.builder().name("Accuracy First").description("Achieve 90% accuracy").criteriaType("CORRECT_ANSWERS").criteriaValue(50).xpReward(0).build(),
                Achievement.builder().name("Rising Star").description("Earn 500 XP").criteriaType("TOTAL_XP").criteriaValue(500).xpReward(100).build(),
                Achievement.builder().name("Centurion").description("Earn 10000 XP").criteriaType("TOTAL_XP").criteriaValue(10000).xpReward(1000).build()
        ));
        log.info("Created 10 achievements.");
    }

    private void seedDailyMissions() {
        if (dailyMissionRepository.count() > 0) return;
        log.info("Seeding daily missions...");
        dailyMissionRepository.saveAll(List.of(
                DailyMission.builder().title("Daily Quiz").description("Complete 3 quizzes today").missionType("QUIZZES_TAKEN").targetValue(3).xpReward(50).build(),
                DailyMission.builder().title("Accuracy Challenge").description("Get 10 correct answers").missionType("CORRECT_ANSWERS").targetValue(10).xpReward(40).build(),
                DailyMission.builder().title("XP Boost").description("Earn 100 XP today").missionType("XP_EARNED").targetValue(100).xpReward(30).build(),
                DailyMission.builder().title("Streak Keeper").description("Complete any quiz").missionType("QUIZZES_TAKEN").targetValue(1).xpReward(20).build()
        ));
        log.info("Created 4 daily missions.");
    }

    private void seedWeeklyMissions() {
        if (weeklyMissionRepository.count() > 0) return;
        log.info("Seeding weekly missions...");
        weeklyMissionRepository.saveAll(List.of(
                WeeklyMission.builder().title("Weekly Warrior").description("Complete 15 quizzes this week").missionType("QUIZZES_TAKEN").targetValue(15).xpReward(300).build(),
                WeeklyMission.builder().title("Accuracy Expert").description("Get 50 correct answers").missionType("CORRECT_ANSWERS").targetValue(50).xpReward(200).build(),
                WeeklyMission.builder().title("XP Grind").description("Earn 1000 XP this week").missionType("XP_EARNED").targetValue(1000).xpReward(500).build(),
                WeeklyMission.builder().title("Contest Champion").description("Win 1 contest").missionType("CONTESTS_WON").targetValue(1).xpReward(400).build()
        ));
        log.info("Created 4 weekly missions.");
    }

    private Map<String, User> seedUsers(Map<String, Role> roleMap) {
        if (userRepository.count() > 0) {
            return userRepository.findAll().stream().collect(Collectors.toMap(User::getUsername, u -> u));
        }
        log.info("Seeding users...");

        Role superAdminRole = roleMap.get("ROLE_SUPER_ADMIN");
        Role adminRole = roleMap.get("ROLE_ADMIN");
        Role userRole = roleMap.get("ROLE_USER");

        User superUser = User.builder()
                .username("superadmin")
                .password(passwordEncoder.encode("superadmin123"))
                .email("superadmin@quizapp.com")
                .displayName("Super Admin")
                .phoneNumber("0000000000")
                .address("System")
                .role(superAdminRole)
                .build();

        User player1 = User.builder()
                .username("john_doe")
                .password(passwordEncoder.encode("password123"))
                .email("john@example.com")
                .displayName("John Doe")
                .phoneNumber("1111111111")
                .address("123 Main St")
                .role(userRole)
                .build();

        User moderator = User.builder()
                .username("moderator")
                .password(passwordEncoder.encode("moderator123"))
                .email("moderator@quizapp.com")
                .displayName("Moderator")
                .phoneNumber("2222222222")
                .address("Moderation Team")
                .role(adminRole)
                .build();

        userRepository.saveAll(List.of(superUser, player1, moderator));
        log.info("Created 3 users: superadmin, john_doe, moderator");
        return Map.of("superadmin", superUser, "john_doe", player1, "moderator", moderator);
    }

    private void seedUserStatistics(Map<String, User> userMap) {
        if (userStatisticsRepository.count() > 0) return;
        log.info("Seeding user statistics...");
        for (User user : userMap.values()) {
            userStatisticsRepository.save(UserStatistics.builder()
                    .userId(user.getId())
                    .totalQuizzesTaken(0).totalQuizzesPassed(0)
                    .totalQuestionsAnswered(0).totalCorrectAnswers(0)
                    .totalScore(0L).totalXp(0L)
                    .currentStreak(0).longestStreak(0)
                    .totalContestsParticipated(0).totalContestsWon(0)
                    .badgesCount(0).achievementsCount(0)
                    .build());
        }
        log.info("Created statistics for {} users.", userMap.size());
    }

    private void seedQuizzes(Map<String, Category> categoryMap, Map<String, Question> questionMap, Map<String, User> userMap) {
        if (quizRepository.count() > 0) return;
        log.info("Seeding quizzes...");

        Category programming = categoryMap.get("Programming");
        Category science = categoryMap.get("Science");
        Category mathematics = categoryMap.get("Mathematics");
        Category generalKnowledge = categoryMap.get("General Knowledge");
        User superUser = userMap.get("superadmin");

        // Quiz 1: Programming Basics (existing)
        Quiz programmingQuiz = Quiz.builder()
                .title("Programming Basics")
                .description("Test your knowledge of basic programming concepts.")
                .category(programming).difficulty("EASY")
                .timeLimitMinutes(10).passingScorePct(60.0)
                .maxAttempts(3).isRandomized(true).isPublished(true).isActive(true)
                .totalQuestions(2).totalPoints(20)
                .createdBy(superUser).build();
        quizRepository.save(programmingQuiz);
        quizQuestionRepository.saveAll(List.of(
                QuizQuestion.builder().quiz(programmingQuiz).questionId(questionMap.get("What is the correct way to declare a variable in Java?").getId()).sortOrder(1).build(),
                QuizQuestion.builder().quiz(programmingQuiz).questionId(questionMap.get("Which of these is not an OOP principle?").getId()).sortOrder(2).build()
        ));

        // Quiz 2: Science Fundamentals
        Quiz scienceQuiz = Quiz.builder()
                .title("Science Fundamentals")
                .description("Explore basic concepts in physics, chemistry, and biology.")
                .category(science).difficulty("EASY")
                .timeLimitMinutes(10).passingScorePct(60.0)
                .maxAttempts(3).isRandomized(true).isPublished(true).isActive(true)
                .totalQuestions(3).totalPoints(30)
                .createdBy(superUser).build();
        quizRepository.save(scienceQuiz);
        quizQuestionRepository.saveAll(List.of(
                QuizQuestion.builder().quiz(scienceQuiz).questionId(questionMap.get("What is the chemical symbol for water?").getId()).sortOrder(1).build(),
                QuizQuestion.builder().quiz(scienceQuiz).questionId(questionMap.get("What planet is known as the Red Planet?").getId()).sortOrder(2).build(),
                QuizQuestion.builder().quiz(scienceQuiz).questionId(questionMap.get("What gas do plants primarily absorb from the atmosphere?").getId()).sortOrder(3).build()
        ));

        // Quiz 3: Math Challenge
        Quiz mathQuiz = Quiz.builder()
                .title("Math Challenge")
                .description("Test your mathematics skills with these problems.")
                .category(mathematics).difficulty("EASY")
                .timeLimitMinutes(10).passingScorePct(60.0)
                .maxAttempts(3).isRandomized(true).isPublished(true).isActive(true)
                .totalQuestions(2).totalPoints(20)
                .createdBy(superUser).build();
        quizRepository.save(mathQuiz);
        quizQuestionRepository.saveAll(List.of(
                QuizQuestion.builder().quiz(mathQuiz).questionId(questionMap.get("What is the value of Pi (\u03c0) to 2 decimal places?").getId()).sortOrder(1).build(),
                QuizQuestion.builder().quiz(mathQuiz).questionId(questionMap.get("What is the square root of 144?").getId()).sortOrder(2).build()
        ));

        // Quiz 4: General Knowledge Quiz
        Quiz gkQuiz = Quiz.builder()
                .title("General Knowledge Quiz")
                .description("How well do you know the world around you?")
                .category(generalKnowledge).difficulty("MEDIUM")
                .timeLimitMinutes(10).passingScorePct(60.0)
                .maxAttempts(3).isRandomized(true).isPublished(true).isActive(true)
                .totalQuestions(3).totalPoints(30)
                .createdBy(superUser).build();
        quizRepository.save(gkQuiz);
        quizQuestionRepository.saveAll(List.of(
                QuizQuestion.builder().quiz(gkQuiz).questionId(questionMap.get("Which country has the largest population?").getId()).sortOrder(1).build(),
                QuizQuestion.builder().quiz(gkQuiz).questionId(questionMap.get("What is the capital of Japan?").getId()).sortOrder(2).build(),
                QuizQuestion.builder().quiz(gkQuiz).questionId(questionMap.get("Who wrote Romeo and Juliet?").getId()).sortOrder(3).build()
        ));

        // Quiz 5: Programming Mastery (hard)
        Quiz masteryQuiz = Quiz.builder()
                .title("Programming Mastery")
                .description("Advanced programming concepts for experienced developers.")
                .category(programming).difficulty("HARD")
                .timeLimitMinutes(15).passingScorePct(70.0)
                .maxAttempts(2).isRandomized(false).isPublished(true).isActive(true)
                .totalQuestions(3).totalPoints(45)
                .createdBy(superUser).build();
        quizRepository.save(masteryQuiz);
        quizQuestionRepository.saveAll(List.of(
                QuizQuestion.builder().quiz(masteryQuiz).questionId(questionMap.get("What is the time complexity of binary search?").getId()).sortOrder(1).build(),
                QuizQuestion.builder().quiz(masteryQuiz).questionId(questionMap.get("Which design pattern ensures a class has only one instance?").getId()).sortOrder(2).build(),
                QuizQuestion.builder().quiz(masteryQuiz).questionId(questionMap.get("Which of these is not an OOP principle?").getId()).sortOrder(3).build()
        ));

        log.info("Created 5 quizzes with questions.");
    }

    private void seedQuizAttempts(Map<String, User> userMap) {
        if (quizAttemptRepository.count() > 0) return;
        log.info("Seeding quiz attempts...");

        Quiz programmingQuiz = quizRepository.findByTitle("Programming Basics").orElse(null);
        Quiz scienceQuiz = quizRepository.findByTitle("Science Fundamentals").orElse(null);
        if (programmingQuiz == null || scienceQuiz == null) return;

        User superUser = userMap.get("superadmin");
        User player1 = userMap.get("john_doe");
        Instant now = Instant.now();

        QuizAttempt attempt1 = QuizAttempt.builder()
                .quizId(programmingQuiz.getId()).userId(superUser.getId())
                .startedAt(now.minusSeconds(7200))
                .submittedAt(now.minusSeconds(6600))
                .timeTakenSeconds(600)
                .score(20).maxScore(20).scorePct(100.0).passed(true)
                .status("COMPLETED").build();
        quizAttemptRepository.save(attempt1);

        QuizAttempt attempt2 = QuizAttempt.builder()
                .quizId(programmingQuiz.getId()).userId(player1.getId())
                .startedAt(now.minusSeconds(10800))
                .submittedAt(now.minusSeconds(10500))
                .timeTakenSeconds(300)
                .score(10).maxScore(20).scorePct(50.0).passed(false)
                .status("COMPLETED").build();
        quizAttemptRepository.save(attempt2);

        QuizAttempt attempt3 = QuizAttempt.builder()
                .quizId(scienceQuiz.getId()).userId(player1.getId())
                .startedAt(now.minusSeconds(3600))
                .submittedAt(now.minusSeconds(3000))
                .timeTakenSeconds(600)
                .score(20).maxScore(30).scorePct(66.67).passed(true)
                .status("COMPLETED").build();
        quizAttemptRepository.save(attempt3);

        log.info("Created {} quiz attempts.", 3);
    }

    private void seedContests(Map<String, User> userMap) {
        if (contestRepository.count() > 0) return;
        log.info("Seeding contests...");

        User superUser = userMap.get("superadmin");
        User player1 = userMap.get("john_doe");
        Instant now = Instant.now();

        Contest contest = Contest.builder()
                .title("Weekend Quiz Championship")
                .description("Compete with other players in a weekend-long quiz challenge!")
                .contestType("TIMED")
                .numQuestions(10)
                .timeLimitMinutes(30)
                .startsAt(now.minusSeconds(86400))
                .endsAt(now.plusSeconds(86400))
                .isActive(true)
                .maxParticipants(50)
                .createdBy(superUser.getId())
                .build();
        contestRepository.save(contest);

        contestParticipantRepository.saveAll(List.of(
                ContestParticipant.builder().contestId(contest.getId()).userId(superUser.getId()).build(),
                ContestParticipant.builder().contestId(contest.getId()).userId(player1.getId()).build()
        ));

        log.info("Created 1 contest with 2 participants.");
    }

    private void seedFriendships(Map<String, User> userMap) {
        if (friendshipRepository.count() > 0) return;
        log.info("Seeding friendships...");

        User superUser = userMap.get("superadmin");
        User player1 = userMap.get("john_doe");
        User moderator = userMap.get("moderator");

        friendshipRepository.saveAll(List.of(
                Friendship.builder().requesterId(superUser.getId()).addresseeId(player1.getId()).status("ACCEPTED").build(),
                Friendship.builder().requesterId(player1.getId()).addresseeId(superUser.getId()).status("ACCEPTED").build(),
                Friendship.builder().requesterId(superUser.getId()).addresseeId(moderator.getId()).status("ACCEPTED").build()
        ));
        log.info("Created 3 friendships.");
    }
}
