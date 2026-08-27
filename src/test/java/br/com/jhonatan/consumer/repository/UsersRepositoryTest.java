package br.com.jhonatan.consumer.repository;

import br.com.jhonatan.consumer.model.Users;
import br.com.jhonatan.consumer.util.UserCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for UsersRepository")
class UsersRepositoryTest {

    @Autowired
    UsersRepository usersRepository;

    @Test
    @DisplayName("Save creates user when successful")
    void save_CreatesUser_WhenSuccessful(){
        Users userToBeSaved = UserCreator.createUserToBeSaved();

        Users savedUser = this.usersRepository.save(userToBeSaved);

        Assertions.assertThat(savedUser).isNotNull();

        Assertions.assertThat(savedUser.getId()).isNotNull();

        Assertions.assertThat(savedUser.getName()).isEqualTo(userToBeSaved.getName());
    }

    @Test
    @DisplayName("Save updates user when successful")
    void save_UpdatesUser_WhenSuccessful(){
        Users userToBeSaved = UserCreator.createUserToBeSaved();

        Users savedUser = this.usersRepository.save(userToBeSaved);

        savedUser.setName("User test updated name");

        Users updatedUser = this.usersRepository.save(savedUser);

        Assertions.assertThat(updatedUser).isNotNull();

        Assertions.assertThat(updatedUser.getId()).isNotNull();

        Assertions.assertThat(updatedUser.getName()).isEqualTo(userToBeSaved.getName());
    }

    @Test
    @DisplayName("Delete removes user when successful")
    void delete_RemovesUser_WhenSuccessful(){

        Users userToBeSaved = UserCreator.createUserToBeSaved();

        Users savedUser = this.usersRepository.save(userToBeSaved);

        this.usersRepository.delete(savedUser);

        Optional<Users> deletedUser = this.usersRepository.findById(savedUser.getId());

        Assertions.assertThat(deletedUser).isEmpty();

        Assertions.assertThat(deletedUser).isNotPresent();
    }

    @Test
    @DisplayName("Find by document returns user when successful")
    void find_ReturnsUser_WhenSuccessful(){
        Users userToBeSaved = UserCreator.createUserToBeSaved();

        Users savedUser = this.usersRepository.save(userToBeSaved);

        Optional<Users> userToBeFound = this.usersRepository.findByDocument(savedUser.getDocument());

        Assertions.assertThat(userToBeFound).isNotEmpty();

        Assertions.assertThat(userToBeFound).isPresent();

        Assertions.assertThat(userToBeFound.get().getDocument()).isEqualTo(userToBeSaved.getDocument());
    }

    @Test
    @DisplayName("Find by document returns empty when no user is found ")
    void find_ReturnsEmpty_WhenSuccessful(){

        Optional<Users> userToBeFound = this.usersRepository.findByDocument("nonExistentDocument");

        Assertions.assertThat(userToBeFound).isEmpty();

        Assertions.assertThat(userToBeFound).isNotPresent();
    }
}