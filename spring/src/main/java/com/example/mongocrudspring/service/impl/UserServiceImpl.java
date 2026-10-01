package com.example.mongocrudspring.service.impl;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private UserProducer producer;

    @Override
    public User createUser(User user) {
        User saved = repo.save(user);
        producer.sendMessage("User Created: " + saved.getId());
        return saved;
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User getUserById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User updateUser(String id, User user) {
        User existing = getUserById(id);
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());

        User updated = repo.save(existing);
        producer.sendMessage("User Updated: " + updated.getId());

        return updated;
    }

    @Override
    public void deleteUser(String id) {
        User user = getUserById(id);
        repo.delete(user);
        producer.sendMessage("User Deleted: " + id);
    }
}