package uniVerse.posterPlot;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisTest {
    private final RedisConnectionFactory redisConnectionFactory;

    public RedisTest(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public void testConnection() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String pingResult = connection.ping();
            System.out.println("Redis ping result: " + pingResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
