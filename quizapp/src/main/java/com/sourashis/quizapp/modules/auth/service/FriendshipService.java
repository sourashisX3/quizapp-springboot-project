package com.sourashis.quizapp.modules.auth.service;

import com.sourashis.quizapp.modules.auth.entity.Friendship;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.exception.UserNotFoundException;
import com.sourashis.quizapp.modules.auth.repository.FriendshipRepository;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import com.sourashis.quizapp.modules.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public void sendRequest(Long requesterId, Long addresseeId) {
        if (requesterId.equals(addresseeId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        if (!userRepository.existsById(addresseeId)) {
            throw new UserNotFoundException(addresseeId);
        }

        Optional<Friendship> existing = friendshipRepository.findByRequesterIdAndAddresseeId(requesterId, addresseeId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Friend request already exists");
        }

        Optional<Friendship> reverse = friendshipRepository.findByRequesterIdAndAddresseeId(addresseeId, requesterId);
        if (reverse.isPresent()) {
            Friendship rev = reverse.get();
            if ("ACCEPTED".equals(rev.getStatus())) {
                throw new IllegalStateException("Already friends");
            }
            if ("PENDING".equals(rev.getStatus())) {
                rev.setStatus("ACCEPTED");
                friendshipRepository.save(rev);
                return;
            }
        }

        Friendship friendship = Friendship.builder()
                .requesterId(requesterId)
                .addresseeId(addresseeId)
                .status("PENDING")
                .build();
        friendshipRepository.save(friendship);

        notificationService.sendToUser(addresseeId, "FRIEND_REQUEST",
                "New Friend Request",
                "You have received a new friend request.",
                "NORMAL");
    }

    public void acceptRequest(Long userId, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new NoSuchElementException("Friendship not found"));

        if (!friendship.getAddresseeId().equals(userId)) {
            throw new IllegalStateException("Not authorized to accept this request");
        }

        if (!"PENDING".equals(friendship.getStatus())) {
            throw new IllegalStateException("Friend request is not pending");
        }

        friendship.setStatus("ACCEPTED");
        friendshipRepository.save(friendship);
    }

    public void rejectRequest(Long userId, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new NoSuchElementException("Friendship not found"));

        if (!friendship.getAddresseeId().equals(userId)) {
            throw new IllegalStateException("Not authorized to reject this request");
        }

        friendshipRepository.delete(friendship);
    }

    public void removeFriend(Long userId, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new NoSuchElementException("Friendship not found"));

        if (!friendship.getRequesterId().equals(userId) && !friendship.getAddresseeId().equals(userId)) {
            throw new IllegalStateException("Not authorized to remove this friendship");
        }

        friendshipRepository.delete(friendship);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFriends(Long userId) {
        List<Friendship> friendships = friendshipRepository
                .findByRequesterIdOrAddresseeId(userId, userId);

        List<Map<String, Object>> friends = new ArrayList<>();
        for (Friendship f : friendships) {
            if (!"ACCEPTED".equals(f.getStatus())) continue;

            Long friendId = f.getRequesterId().equals(userId) ? f.getAddresseeId() : f.getRequesterId();
            User friend = userRepository.findById(friendId).orElse(null);
            if (friend == null) continue;

            Map<String, Object> friendDto = new HashMap<>();
            friendDto.put("id", friend.getId());
            friendDto.put("username", friend.getUsername());
            friendDto.put("displayName", friend.getDisplayName());
            friendDto.put("profilePictureUrl", friend.getProfilePictureUrl());
            friends.add(friendDto);
        }
        return friends;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPendingRequests(Long userId) {
        List<Friendship> pending = friendshipRepository
                .findByAddresseeIdAndStatus(userId, "PENDING");

        List<Map<String, Object>> result = new ArrayList<>();
        for (Friendship f : pending) {
            User requester = userRepository.findById(f.getRequesterId()).orElse(null);
            if (requester == null) continue;

            Map<String, Object> dto = new HashMap<>();
            dto.put("friendshipId", f.getId());
            dto.put("id", requester.getId());
            dto.put("username", requester.getUsername());
            dto.put("displayName", requester.getDisplayName());
            dto.put("profilePictureUrl", requester.getProfilePictureUrl());
            dto.put("createdAt", f.getCreatedAt());
            result.add(dto);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSentRequests(Long userId) {
        List<Friendship> sent = friendshipRepository
                .findByRequesterIdAndStatus(userId, "PENDING");

        List<Map<String, Object>> result = new ArrayList<>();
        for (Friendship f : sent) {
            User addressee = userRepository.findById(f.getAddresseeId()).orElse(null);
            if (addressee == null) continue;

            Map<String, Object> dto = new HashMap<>();
            dto.put("friendshipId", f.getId());
            dto.put("id", addressee.getId());
            dto.put("username", addressee.getUsername());
            dto.put("displayName", addressee.getDisplayName());
            dto.put("profilePictureUrl", addressee.getProfilePictureUrl());
            dto.put("createdAt", f.getCreatedAt());
            result.add(dto);
        }
        return result;
    }
}
