package dev.apolo.redis;

import dev.apolo.api.exception.ApoloRedisException;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

@Slf4j
public class RedisManager {
    private final JedisPool pool;

    public RedisManager(String host, int port, String password, int database,
                        int maxTotal, int maxIdle, int minIdle) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        if (password != null && !password.isEmpty()) {
            this.pool = new JedisPool(config, host, port, 2000, password, database);
        } else {
            this.pool = new JedisPool(config, host, port, 2000, null, database);
        }
    }

    public String get(String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            throw new ApoloRedisException("Error getting key: " + key, e);
        }
    }

    public void set(String key, String value) {
        try (Jedis jedis = pool.getResource()) {
            jedis.set(key, value);
        } catch (Exception e) {
            throw new ApoloRedisException("Error setting key: " + key, e);
        }
    }

    public void setEx(String key, int seconds, String value) {
        try (Jedis jedis = pool.getResource()) {
            jedis.setex(key, seconds, value);
        } catch (Exception e) {
            throw new ApoloRedisException("Error setting key with TTL: " + key, e);
        }
    }

    public void delete(String key) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            throw new ApoloRedisException("Error deleting key: " + key, e);
        }
    }

    public boolean exists(String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            throw new ApoloRedisException("Error checking existence of key: " + key, e);
        }
    }

    public void sadd(String key, String... members) {
        try (Jedis jedis = pool.getResource()) {
            jedis.sadd(key, members);
        } catch (Exception e) {
            throw new ApoloRedisException("Error adding to set: " + key, e);
        }
    }

    public Set<String> smembers(String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(key);
        } catch (Exception e) {
            throw new ApoloRedisException("Error getting set members: " + key, e);
        }
    }

    public void srem(String key, String... members) {
        try (Jedis jedis = pool.getResource()) {
            jedis.srem(key, members);
        } catch (Exception e) {
            throw new ApoloRedisException("Error removing from set: " + key, e);
        }
    }

    public boolean sismember(String key, String member) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.sismember(key, member);
        } catch (Exception e) {
            throw new ApoloRedisException("Error checking set membership: " + key, e);
        }
    }

    public long ttl(String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.ttl(key);
        } catch (Exception e) {
            throw new ApoloRedisException("Error getting TTL for key: " + key, e);
        }
    }

    public void publish(String channel, String message) {
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, message);
        } catch (Exception e) {
            throw new ApoloRedisException("Error publishing to channel: " + channel, e);
        }
    }

    public void shutdown() {
        if (pool != null && !pool.isClosed()) {
            pool.close();
        }
    }

    public JedisPool getPool() {
        return pool;
    }
}
