This is used in conjunction with the workshop at https://github.com/datastaxdevs/workshop-intro-quarkus-cassandra.

## Mappers vs Raw CQL
This version has a build-time property (not overridable at runtime) called `astra-service.type`. By default, if this property is undefined _OR_ has the value `cql-session`, then [`CqlSessionAstraService`](src/main/java/com/datastaxdev/todo/service/CqlSessionAstraService.java) will be injected as the implementation for [`AstraService`](src/main/java/com/datastaxdev/todo/service/AstraService.java). This version uses hand-crafted CQL queries executed against the `CqlSession`.

If, at build time, `astra-service.type=dao`, then [`MapperAstraService`](src/main/java/com/datastaxdev/todo/service/MapperAstraService.java) will be used instead. This version will use the [Cassandra Entity Modeling](https://quarkus.io/guides/cassandra#creating-the-data-model-and-data-access-objects).

The [`AstraConfig`](src/main/java/com/datastaxdev/todo/config/AstraConfig.java) class contains everything needed for reading this flag at build time and injecting the appropriate [`AstraService`](src/main/java/com/datastaxdev/todo/service/AstraService.java) implementation.

## Blocking vs Async/reactive
A mix of blocking vs reactive endpoints has been done. In [`TodoResource`](src/main/java/com/datastaxdev/todo/rest/TodoResource.java), the `getTodos` (`GET` to `/api/todo/{list_id}`) and `setTodo` (`POST` to `/api/todo/{list_id}`) methods are implemented as reactive methods. This means that their execution happens on the event loop thread, whereas all of the other methods are blocking. Quarkus will offload those executions onto worker threads (read about [Quarkus smart dispatching](https://quarkus.io/blog/resteasy-reactive-smart-dispatch) for more information).

Subsequently, the `getTodos` and `setTodo` methods in [`AstraService`](src/main/java/com/datastaxdev/todo/service/AstraService.java) have been updated to be reactive. Both the CQL and entity mapper implementations have been updated to use the [Cassandra driver's reactive support](https://quarkus.io/guides/cassandra#reactive) as well.

As you can see, blocking & reactive can co-exist in the same class!