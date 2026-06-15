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
import java.util.ArrayList;
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
        Map<String, Question> questionMap = new LinkedHashMap<>();
        for (Question q : questionRepository.findAll()) {
            questionMap.put(q.getTitle(), q);
        }

        Category programming = categoryMap.get("Programming");
        Category science = categoryMap.get("Science");
        Category mathematics = categoryMap.get("Mathematics");
        Category generalKnowledge = categoryMap.get("General Knowledge");

        int created = 0;
        created += createQuestionIfMissing(questionMap, "What is the correct way to declare a variable in Java?",
                programming, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().optionText("int x = 5;").isCorrect(true).sortOrder(1).build(),
                        QuestionOption.builder().optionText("variable x = 5;").isCorrect(false).sortOrder(2).build(),
                        QuestionOption.builder().optionText("x = 5 int;").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().optionText("int x == 5;").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "Which of these is not an OOP principle?",
                programming, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().optionText("Encapsulation").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().optionText("Inheritance").isCorrect(false).sortOrder(2).build(),
                        QuestionOption.builder().optionText("Polymorphism").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().optionText("Compilation").isCorrect(true).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What is the chemical symbol for water?",
                science, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().question(null).optionText("H2O").isCorrect(true).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("CO2").isCorrect(false).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("NaCl").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("O2").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What planet is known as the Red Planet?",
                science, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().question(null).optionText("Venus").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("Mars").isCorrect(true).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("Jupiter").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("Saturn").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What is the value of Pi (\u03c0) to 2 decimal places?",
                mathematics, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().question(null).optionText("3.14").isCorrect(true).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("3.16").isCorrect(false).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("3.12").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("3.18").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What is the square root of 144?",
                mathematics, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().question(null).optionText("10").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("12").isCorrect(true).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("14").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("16").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "Which country has the largest population?",
                generalKnowledge, DifficultyLevel.MEDIUM, 10,
                List.of(
                        QuestionOption.builder().question(null).optionText("India").isCorrect(true).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("USA").isCorrect(false).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("Indonesia").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("China").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What is the capital of Japan?",
                generalKnowledge, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().question(null).optionText("Seoul").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("Beijing").isCorrect(false).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("Tokyo").isCorrect(true).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("Bangkok").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What is the time complexity of binary search?",
                programming, DifficultyLevel.MEDIUM, 15,
                List.of(
                        QuestionOption.builder().question(null).optionText("O(n)").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("O(log n)").isCorrect(true).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("O(n log n)").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("O(n^2)").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "Which design pattern ensures a class has only one instance?",
                programming, DifficultyLevel.HARD, 20,
                List.of(
                        QuestionOption.builder().question(null).optionText("Factory Pattern").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("Observer Pattern").isCorrect(false).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("Singleton Pattern").isCorrect(true).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("Builder Pattern").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What is the speed of light in vacuum (approx)?",
                science, DifficultyLevel.MEDIUM, 15,
                List.of(
                        QuestionOption.builder().question(null).optionText("3 \u00d7 10^6 m/s").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("3 \u00d7 10^8 m/s").isCorrect(true).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("3 \u00d7 10^10 m/s").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("3 \u00d7 10^4 m/s").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What is the atomic number of Carbon?",
                science, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().question(null).optionText("4").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("8").isCorrect(false).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("6").isCorrect(true).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("12").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What is the derivative of x\u00b2?",
                mathematics, DifficultyLevel.MEDIUM, 15,
                List.of(
                        QuestionOption.builder().question(null).optionText("x").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("2x").isCorrect(true).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("x\u00b2").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("2").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "Who wrote Romeo and Juliet?",
                generalKnowledge, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().question(null).optionText("Charles Dickens").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("William Shakespeare").isCorrect(true).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("Jane Austen").isCorrect(false).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("Mark Twain").isCorrect(false).sortOrder(4).build()
                ));
        created += createQuestionIfMissing(questionMap, "What gas do plants primarily absorb from the atmosphere?",
                science, DifficultyLevel.EASY, 10,
                List.of(
                        QuestionOption.builder().question(null).optionText("Oxygen").isCorrect(false).sortOrder(1).build(),
                        QuestionOption.builder().question(null).optionText("Nitrogen").isCorrect(false).sortOrder(2).build(),
                        QuestionOption.builder().question(null).optionText("Carbon Dioxide").isCorrect(true).sortOrder(3).build(),
                        QuestionOption.builder().question(null).optionText("Hydrogen").isCorrect(false).sortOrder(4).build()
                ));

        if (created > 0) {
            log.info("Created {} new questions with options.", created);
        }
        return questionMap;
    }

    private int createQuestionIfMissing(Map<String, Question> questionMap, String title,
                                         Category category, DifficultyLevel difficulty, int points,
                                         List<QuestionOption> options) {
        if (questionMap.containsKey(title)) return 0;
        Question q = questionRepository.save(Question.builder()
                .title(title).category(category).difficulty(difficulty).points(points).build());
        options.forEach(opt -> opt.setQuestion(q));
        questionOptionRepository.saveAll(options);
        questionMap.put(title, q);
        return 1;
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
        Map<String, User> userMap = new LinkedHashMap<>();
        for (User u : userRepository.findAll()) {
            userMap.put(u.getUsername(), u);
        }

        Role superAdminRole = roleMap.get("ROLE_SUPER_ADMIN");
        Role adminRole = roleMap.get("ROLE_ADMIN");
        Role userRole = roleMap.get("ROLE_USER");

        if (!userMap.containsKey("superadmin")) {
            User superUser = userRepository.save(User.builder()
                    .username("superadmin")
                    .password(passwordEncoder.encode("superadmin123"))
                    .email("superadmin@quizapp.com")
                    .displayName("Super Admin")
                    .phoneNumber("0000000000")
                    .address("System")
                    .role(superAdminRole)
                    .build());
            userMap.put("superadmin", superUser);
            log.info("Created superadmin user.");
        }

        if (!userMap.containsKey("john_doe")) {
            User player1 = userRepository.save(User.builder()
                    .username("john_doe")
                    .password(passwordEncoder.encode("password123"))
                    .email("john@example.com")
                    .displayName("John Doe")
                    .phoneNumber("1111111111")
                    .address("123 Main St")
                    .role(userRole)
                    .build());
            userMap.put("john_doe", player1);
            log.info("Created john_doe user.");
        }

        if (!userMap.containsKey("moderator")) {
            User moderator = userRepository.save(User.builder()
                    .username("moderator")
                    .password(passwordEncoder.encode("moderator123"))
                    .email("moderator@quizapp.com")
                    .displayName("Moderator")
                    .phoneNumber("2222222222")
                    .address("Moderation Team")
                    .role(adminRole)
                    .build());
            userMap.put("moderator", moderator);
            log.info("Created moderator user.");
        }

        return userMap;
    }

    private void seedUserStatistics(Map<String, User> userMap) {
        Set<Long> existingUserIds = userStatisticsRepository.findAll().stream()
                .map(UserStatistics::getUserId).collect(Collectors.toSet());
        int created = 0;
        for (User user : userMap.values()) {
            if (existingUserIds.contains(user.getId())) continue;
            userStatisticsRepository.save(UserStatistics.builder()
                    .userId(user.getId())
                    .totalQuizzesTaken(0).totalQuizzesPassed(0)
                    .totalQuestionsAnswered(0).totalCorrectAnswers(0)
                    .totalScore(0L).totalXp(0L)
                    .currentStreak(0).longestStreak(0)
                    .totalContestsParticipated(0).totalContestsWon(0)
                    .badgesCount(0).achievementsCount(0)
                    .build());
            created++;
        }
        if (created > 0) {
            log.info("Created statistics for {} users.", created);
        }
    }

    private void seedQuizzes(Map<String, Category> categoryMap, Map<String, Question> questionMap, Map<String, User> userMap) {
        Category programming = categoryMap.get("Programming");
        Category science = categoryMap.get("Science");
        Category mathematics = categoryMap.get("Mathematics");
        Category generalKnowledge = categoryMap.get("General Knowledge");
        User superUser = userMap.get("superadmin");

        List<QuizDef> quizDefs = List.of(
                new QuizDef("Programming Basics", "Test your knowledge of basic programming concepts.",
                        programming, "EASY", 10, 60.0, 3, true, true, true, 2, 20,
                        List.of(
                                new QRef("What is the correct way to declare a variable in Java?", 1),
                                new QRef("Which of these is not an OOP principle?", 2)
                        )),
                new QuizDef("Science Fundamentals", "Explore basic concepts in physics, chemistry, and biology.",
                        science, "EASY", 10, 60.0, 3, true, true, true, 3, 30,
                        List.of(
                                new QRef("What is the chemical symbol for water?", 1),
                                new QRef("What planet is known as the Red Planet?", 2),
                                new QRef("What gas do plants primarily absorb from the atmosphere?", 3)
                        )),
                new QuizDef("Math Challenge", "Test your mathematics skills with these problems.",
                        mathematics, "EASY", 10, 60.0, 3, true, true, true, 2, 20,
                        List.of(
                                new QRef("What is the value of Pi (\u03c0) to 2 decimal places?", 1),
                                new QRef("What is the square root of 144?", 2)
                        )),
                new QuizDef("General Knowledge Quiz", "How well do you know the world around you?",
                        generalKnowledge, "MEDIUM", 10, 60.0, 3, true, true, true, 3, 30,
                        List.of(
                                new QRef("Which country has the largest population?", 1),
                                new QRef("What is the capital of Japan?", 2),
                                new QRef("Who wrote Romeo and Juliet?", 3)
                        )),
                new QuizDef("Programming Mastery", "Advanced programming concepts for experienced developers.",
                        programming, "HARD", 15, 70.0, 2, false, true, true, 3, 45,
                        List.of(
                                new QRef("What is the time complexity of binary search?", 1),
                                new QRef("Which design pattern ensures a class has only one instance?", 2),
                                new QRef("Which of these is not an OOP principle?", 3)
                        ))
        );

        int created = 0;
        for (QuizDef def : quizDefs) {
            if (!quizRepository.findByTitle(def.title()).isEmpty()) continue;
            Quiz quiz = quizRepository.save(Quiz.builder()
                    .title(def.title()).description(def.description())
                    .category(def.category()).difficulty(def.difficulty())
                    .timeLimitMinutes(def.timeLimitMinutes()).passingScorePct(def.passingScorePct())
                    .maxAttempts(def.maxAttempts()).isRandomized(def.isRandomized())
                    .isPublished(def.isPublished()).isActive(def.isActive())
                    .totalQuestions(def.totalQuestions()).totalPoints(def.totalPoints())
                    .createdBy(superUser).build());
            List<QuizQuestion> qqs = new ArrayList<>();
            for (QRef ref : def.questions()) {
                Question q = questionMap.get(ref.title());
                if (q != null) {
                    qqs.add(QuizQuestion.builder().quiz(quiz).questionId(q.getId()).sortOrder(ref.sortOrder()).build());
                }
            }
            quizQuestionRepository.saveAll(qqs);
            created++;
        }

        if (created > 0) {
            log.info("Created {} new quizzes with questions.", created);
        }
    }

    private record QuizDef(String title, String description, Category category, String difficulty,
                            int timeLimitMinutes, double passingScorePct, int maxAttempts,
                            boolean isRandomized, boolean isPublished, boolean isActive,
                            int totalQuestions, int totalPoints, List<QRef> questions) {}
    private record QRef(String title, int sortOrder) {}

    private void seedQuizAttempts(Map<String, User> userMap) {
        if (quizAttemptRepository.count() > 0) return;
        log.info("Seeding quiz attempts...");

        Quiz programmingQuiz = quizRepository.findByTitle("Programming Basics").stream().findFirst().orElse(null);
        Quiz scienceQuiz = quizRepository.findByTitle("Science Fundamentals").stream().findFirst().orElse(null);
        if (programmingQuiz == null || scienceQuiz == null) return;

        User superUser = userMap.get("superadmin");
        User player1 = userMap.get("john_doe");
        if (superUser == null || player1 == null) return;

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
        if (superUser == null || player1 == null) return;
        Instant now = Instant.now();

        Contest contest = contestRepository.save(Contest.builder()
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
                .build());

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

        List<Friendship> friendships = new ArrayList<>();
        if (superUser != null && player1 != null) {
            friendships.add(Friendship.builder().requesterId(superUser.getId()).addresseeId(player1.getId()).status("ACCEPTED").build());
            friendships.add(Friendship.builder().requesterId(player1.getId()).addresseeId(superUser.getId()).status("ACCEPTED").build());
        }
        if (superUser != null && moderator != null) {
            friendships.add(Friendship.builder().requesterId(superUser.getId()).addresseeId(moderator.getId()).status("ACCEPTED").build());
        }
        if (!friendships.isEmpty()) {
            friendshipRepository.saveAll(friendships);
        }
        log.info("Created {} friendships.", friendships.size());
    }
}
