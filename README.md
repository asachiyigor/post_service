# Программная архитектура

## Общая схема архитектуры

```
                           USERS
                             │
                             ▼
    ┌─────────────────────────────────────────────────────────────┐
    │                    API GATEWAY                              │
    └─────────────────────────────────────────────────────────────┘
                             │
                             ▼
    ┌──────────────────┬──────────────────┬──────────────────────┐
    │                  │                  │                      │
    ▼                  ▼                  ▼                      ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│LikeController│   │CommentCtrl  │   │PostController│   │FeedController│
└─────────────┘   └─────────────┘   └─────────────┘   └─────────────┘
    │                  │                  │                      │
    ▼                  ▼                  ▼                      ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│ LikeService │   │CommentService│   │ PostService │   │ FeedService │
└─────────────┘   └─────────────┘   └─────────────┘   └─────────────┘
    │                  │                  │                      │
    ▼                  ▼                  ▼                      ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│LikeRepository│   │CommentRepo  │   │PostRepository│   │RedisFeedRepo│
└─────────────┘   └─────────────┘   └─────────────┘   └─────────────┘
```

## Компоненты для публикации поста

### Основной поток
```
PostController ──▶ PostService ──▶ PostRepository
                      │
                      ▼
                 KafkaProducer ──▶ KAFKA TOPICS
```

### Redis кеширование
```
PostService ──▶ RedisUserRepository
            └──▶ RedisPostRepository
```

## Kafka Event-Driven Architecture

```
                    ┌─────────────────┐
                    │     KAFKA       │
                    │                 │
                    │  ┌───────────┐  │
                    │  │new-posts  │  │
                    │  │   topic   │  │
                    │  └───────────┘  │
                    │                 │
                    │  ┌───────────┐  │
                    │  │feed-updates│ │
                    │  │   topic   │  │
                    │  └───────────┘  │
                    └─────────────────┘
                           │    │
                           ▼    ▼
        ┌─────────────────────────────────────┐
        │         CONSUMERS                   │
        │                                     │
        │     ┌─────────────────┐             │
        │     │CommentConsumer  │             │
        │     └─────────────────┘             │
        │                                     │
        │     ┌─────────────────┐             │
        │     │  LikeConsumer   │             │
        │     └─────────────────┘             │
        │                                     │
        │     ┌─────────────────┐             │
        │     │   Post          │             │
        │     │   Consumer      │             │
        │     └─────────────────┘             │
        └─────────────────────────────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ FeedHeater  │
                    └─────────────┘
```

## Построение/получение feed

### Feed Controller Flow
```
FeedController ──▶ FeedService ──▶ RedisFeedRepository
                                        │
                                        ▼
                                  ┌─────────────┐
                                  │    FEED     │
                                  │             │
                                  │ ┌─────────┐ │
                                  │ │  Post1  │ │
                                  │ └─────────┘ │
                                  │ ┌─────────┐ │
                                  │ │  Post2  │ │
                                  │ └─────────┘ │
                                  │ ┌─────────┐ │
                                  │ │  Post3  │ │
                                  │ └─────────┘ │
                                  └─────────────┘
```


## Легенда

**Компоненты для публикации поста**
- PostController, PostService, PostRepository
- KafkaProducer для отправки событий

**Компоненты для построения/получения feed**  
- FeedController, FeedService, RedisFeedRepository
- FeedPostEventConsumer для обработки событий

**Прогреватель кеша на случай выключения Redis**
- FeedHeater для восстановления данных

## Основные потоки данных

### 1. Создание поста
```
User Request ──▶ PostController ──▶ PostService ──▶ Database
                                        │
                                        ▼
                                  Kafka Producer ──▶ new-posts topic
```

### 2. Обновление лент
```
new-posts topic ──▶ FeedPostEventConsumer ──▶ RedisFeedRepository
                                                     │
                                                     ▼
                                              Update user feeds
```

### 3. Получение ленты
```
User Request ──▶ FeedController ──▶ FeedService ──▶ RedisFeedRepository
                                                           │
                                                           ▼
                                                    Return sorted posts
```

### 4. Кеширование
```
Service Layer ──▶ Check Redis Cache ──▶ If miss: Load from DB ──▶ Cache result
```

## Преимущества архитектуры

- ✅ **Масштабируемость** - независимые микросервисы
- ✅ **Производительность** - Redis кеширование  
- ✅ **Асинхронность** - Kafka для тяжелых операций
- ✅ **Отказоустойчивость** - FeedHeater для восстановления
- ✅ **Разделение ответственности** - четкие границы сервисов
