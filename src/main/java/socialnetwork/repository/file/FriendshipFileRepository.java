package socialnetwork.repository.file;

import socialnetwork.Utils.Parse;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * File Repository for Friendships
 */
public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {
    /**
     * Constructor calling its superclass constructor {@code AbstractFileRepository}
     * using friendships.csv relative path
     *
     * @param validator friendship validator
     */
    public FriendshipFileRepository(Validator<Friendship> validator) {
        super("data/friendships.csv", validator);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        String id1 = attributes.get(0);
        String id2 = attributes.get(1);
        String date = attributes.get(2);
        Friendship friendship = new Friendship(Parse.safeParseLong(id1), Parse.safeParseLong(id2));
        friendship.setDate(LocalDateTime.parse(date));
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship friendship) {
        return friendship.toString();
    }
}
