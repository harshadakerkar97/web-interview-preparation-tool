package com.interviewprep.web.content;

import com.interviewprep.web.model.Topic;

import java.util.*;

/**
 * Provides all content for the web app. Replicates the structure from the desktop app's
 * CoreJavaDetailed and AdvancedJavaDetailed content classes.
 *
 * To keep this file manageable, it includes representative topics. The full content
 * from the desktop app can be ported incrementally.
 */
public class ContentProvider {

    private static final Map<String, List<Topic>> SECTIONS = new LinkedHashMap<>();

    static {
        SECTIONS.put("Core Java", buildCoreJava());
        SECTIONS.put("Advanced Java", buildAdvancedJava());
    }

    public static Map<String, List<Topic>> getSections() { return SECTIONS; }
    public static List<String> getSectionNames() { return List.copyOf(SECTIONS.keySet()); }

    public static List<Topic> getTopicsForSection(String section) {
        return SECTIONS.getOrDefault(section, List.of());
    }

    // ==================== Core Java ====================

    private static List<Topic> buildCoreJava() {
        List<Topic> topics = new ArrayList<>();

        topics.add(new Topic("JVM Architecture, ClassLoaders & Memory Model")
            .category("Core Java").difficulty("Advanced")
            .definition("The Java Virtual Machine (JVM) is an abstract computing machine that loads, verifies, and executes Java bytecode. The JDK is the development kit, the JRE is the runtime, and the JVM is the engine that actually runs the .class files.")
            .whyItMatters("Nearly every hard production problem traces back to the JVM: OutOfMemoryError, long GC pauses, ClassNotFoundException, memory leaks, and slow startup.")
            .howItWorks("Execution flows through three subsystems:\n\n1. ClassLoader Subsystem - Loading, Linking, Initialization\n2. Runtime Data Areas - Method Area (Metaspace), Heap, Stack, PC Register, Native Method Stack\n3. Execution Engine - Interpreter, JIT compiler, and Garbage Collector")
            .internalWorking("Memory layout:\n\n* Heap - shared across all threads; holds objects. Split into Young Gen (Eden + Survivor) and Old Gen.\n* Metaspace - class metadata in native memory since Java 8.\n* Stack - one per thread; holds frames. StackOverflowError comes from here.\n* PC Register - per thread; address of current instruction.\n\nGC: Minor GC cleans Young Gen; Full GC cleans Old Gen. Modern collectors: G1, ZGC, Shenandoah.")
            .syntax("# Heap sizing\njava -Xms512m -Xmx2g -jar app.jar\n\n# GC logging\njava -Xlog:gc*:file=gc.log -jar app.jar\n\n# Heap dump on OOM\njava -XX:+HeapDumpOnOutOfMemoryError -jar app.jar")
            .codeExample("// ClassLoader hierarchy\nClassLoader app = Demo.class.getClassLoader();\nSystem.out.println(app);              // AppClassLoader\nSystem.out.println(app.getParent());  // PlatformClassLoader\nSystem.out.println(app.getParent().getParent()); // null (Bootstrap)\n\n// Runtime memory\nRuntime rt = Runtime.getRuntime();\nlong used = (rt.totalMemory() - rt.freeMemory()) / (1024*1024);\nSystem.out.printf(\"Used: %d MB%n\", used);")
            .realWorldExample("A Spring Boot pod kept getting OOMKilled despite healthy heap usage. The cause was off-heap memory: 200 threads at 1MB stack each = 200MB. Fix: set -XX:MaxRAMPercentage=70 and cap the thread pool.")
            .advantages(Arrays.asList("Platform independence", "Automatic memory management", "JIT can beat static compilation", "Strong bytecode verification", "Mature observability tools"))
            .disadvantages(Arrays.asList("Slower startup than native", "GC pauses can violate latency budgets", "Higher memory footprint", "Tuning surface is large"))
            .whenToUse("Tune deliberately in containers, when latency p99 matters, when diagnosing OOM or leaks.")
            .whenNotToUse("Don't guess at flags. Don't copy tuning from blog posts without measuring.")
            .commonMistakes(Arrays.asList("Setting -Xmx to full container memory", "Confusing ClassNotFoundException vs NoClassDefFoundError", "Believing System.gc() forces collection", "Treating large heap as a fix for a leak"))
            .interviewAnswer("The JVM has three parts: ClassLoader (load-link-init with parent-first delegation), Runtime Data Areas (Heap shared, Stack/PC per-thread, Metaspace native), and Execution Engine (interpreter + C1/C2 JIT + GC). StackOverflowError = stack, OutOfMemoryError = heap. Use -XX:MaxRAMPercentage in containers.")
            .followUpQuestions(Arrays.asList("What replaced PermGen in Java 8?", "How does G1 differ from Parallel GC?", "What are GC roots?", "Explain escape analysis"))
            .scenarioQuestions(Arrays.asList("Pod OOMKilled but heap is 40%. Investigate.", "p99 spikes every few minutes. Diagnose.", "Memory grows over 48 hours. Process?"))
            .quickRevision("JDK > JRE > JVM. ClassLoader (parent-first), Runtime Data Areas (Heap shared, Stack per-thread, Metaspace native), Execution Engine (JIT + GC). Heap = Young + Old. Minor GC copies, Full GC mark-sweep-compact. StackOverflowError = stack, OOM = heap."));

        topics.add(new Topic("OOP: Encapsulation, Inheritance, Polymorphism, Abstraction")
            .category("Core Java").difficulty("Intermediate")
            .definition("OOP organises software around objects bundling state (fields) with behaviour (methods). Java implements four pillars: encapsulation, inheritance, polymorphism, and abstraction.")
            .whyItMatters("Every Java framework uses these mechanics. Spring DI works through interfaces. Hibernate uses runtime subclass proxies. Understanding dynamic dispatch tells you which method actually runs.")
            .howItWorks("Encapsulation - private fields, controlled access via methods.\nInheritance - extends one class, implements many interfaces.\nPolymorphism - overloading (compile-time) vs overriding (runtime via vtable).\nAbstraction - abstract classes (partial impl + state) vs interfaces (contract + defaults).")
            .internalWorking("Dynamic dispatch: each class has a vtable. invokevirtual looks up the receiver's actual type. Static methods use invokestatic (hidden, not overridden). Fields are resolved statically. Calling an overridable method from a constructor is dangerous - the subclass override runs before subclass fields init.")
            .syntax("public abstract class Shape {\n    public abstract double area();\n}\n\npublic interface Validator<T> {\n    boolean isValid(T value);\n    default String message() { return \"invalid\"; }\n}\n\npublic sealed interface Payment permits Card, Upi { }")
            .codeExample("// Runtime polymorphism\nAnimal a = new Dog();\na.speak(); // Dog's implementation via vtable\n\n// Static methods are HIDDEN not overridden\nParent p = new Child();\nParent.who(); // Parent - resolved statically\np.instanceWho(); // Child - resolved dynamically\n\n// Fields are NOT polymorphic\nclass A { String name = \"A\"; }\nclass B extends A { String name = \"B\"; }\nA ref = new B();\nSystem.out.println(ref.name); // \"A\"")
            .realWorldExample("A Spring Boot payments service used PaymentProcessor interface with Card, UPI, and Wallet implementations. Adding a new provider = one @Component class, no existing code changed.")
            .advantages(Arrays.asList("Code reusability", "Modularity", "Design patterns", "Testability through interfaces"))
            .disadvantages(Arrays.asList("Deep hierarchies are fragile", "Over-abstraction", "Virtual dispatch cost"))
            .whenToUse("Interfaces for substitutable behaviour. Abstract classes for shared state + partial algorithm. Sealed types for closed hierarchies.")
            .whenNotToUse("Don't use inheritance purely for code reuse - prefer composition.")
            .commonMistakes(Arrays.asList("Believing static methods override", "Expecting fields to be polymorphic", "Calling overridable methods from constructors", "Overriding equals without hashCode"))
            .interviewAnswer("Four pillars. Polymorphism has two forms: overloading is compile-time (static types decide), overriding is runtime (vtable lookup on actual object type). Static methods hide, not override. Fields are never polymorphic. Prefer composition over inheritance. Never call overridable methods from constructors.")
            .followUpQuestions(Arrays.asList("Abstract class vs interface post-Java 8?", "What are covariant return types?", "Explain Liskov Substitution Principle"))
            .scenarioQuestions(Arrays.asList("Override throws NPE on a clearly initialised field. Explain.", "Objects vanish from HashSet after field update. Why?"))
            .quickRevision("Encapsulation (private + controlled access), Inheritance (single class, multiple interfaces), Polymorphism (overloading=compile-time, overriding=runtime/vtable), Abstraction (abstract class vs interface). Static methods hide. Fields not polymorphic. Never call overridable from constructor."));

        topics.add(new Topic("Collections Framework: List, Set, Queue, Map")
            .category("Core Java").difficulty("Intermediate")
            .definition("The Java Collections Framework provides interfaces and implementations for storing groups of objects. Root: Collection (List, Set, Queue) plus Map separately.")
            .whyItMatters("Choosing the wrong collection is the most common performance mistake. O(n) contains inside a loop = O(n^2). Framework code is built on these types.")
            .howItWorks("List - ordered, index-addressable, duplicates allowed (ArrayList, LinkedList).\nSet - no duplicates (HashSet, TreeSet, LinkedHashSet).\nQueue/Deque - end-based access (ArrayDeque, PriorityQueue).\nMap - key-value pairs (HashMap, TreeMap, ConcurrentHashMap).")
            .internalWorking("ArrayList: Object[] with size, grows 1.5x, get O(1), insert O(n).\nHashMap: array of buckets, hash & (n-1), treeifies at 8 entries, O(1) average.\nTreeMap: red-black tree, O(log n), sorted.\nConcurrentHashMap: per-bin CAS + synchronized, lock-free reads.")
            .syntax("Map<String, Integer> map = new HashMap<>(1000);\nmap.computeIfAbsent(key, k -> expensive(k));\nmap.merge(key, 1, Integer::sum);\n\nList<String> immutable = List.of(\"a\", \"b\");\nSet<String> set = new HashSet<>(list);\nDeque<Integer> stack = new ArrayDeque<>();")
            .codeExample("// The #1 performance mistake\nList<String> banned = loadBannedIds(); // 100k\nfor (String id : incoming) {\n    if (banned.contains(id)) reject(id); // O(n) each = 10^10\n}\n// Fix: Set gives O(1)\nSet<String> bannedSet = new HashSet<>(banned);\n\n// Safe removal during iteration\nnums.removeIf(n -> n % 2 == 0);\n\n// LRU cache in 6 lines\nclass LruCache<K,V> extends LinkedHashMap<K,V> {\n    private final int max;\n    LruCache(int max) { super(16, 0.75f, true); this.max = max; }\n    protected boolean removeEldestEntry(Map.Entry<K,V> e) { return size() > max; }\n}")
            .realWorldExample("An order service degraded from 3000 to 200 RPS as data grew. 80% CPU was in ArrayList.indexOf - 50k products searched linearly per request. HashMap fixed it instantly.")
            .advantages(Arrays.asList("Consistent API", "Dynamic sizing", "Generics type safety", "Documented complexity", "Fail-fast iterators"))
            .disadvantages(Arrays.asList("Boxing overhead for primitives", "Most are not thread-safe", "Wrong choice is silent until scale"))
            .whenToUse("ArrayList for most lists. HashMap for lookups. HashSet for membership. TreeMap for sorted/range. ArrayDeque for stack/queue. ConcurrentHashMap for shared maps.")
            .whenNotToUse("Avoid LinkedList. Avoid Vector/Hashtable/Stack. Don't share HashMap across threads.")
            .commonMistakes(Arrays.asList("List.contains in a loop instead of Set", "Modifying during iteration", "Mutable HashMap keys", "equals without hashCode", "Sharing HashMap across threads"))
            .interviewAnswer("List = ordered + duplicates (ArrayList). Set = unique (HashSet). Map = key-value (HashMap). The #1 bug is List.contains in a loop = O(n^2). Use Set for O(1) membership. ArrayList beats LinkedList in practice. ConcurrentHashMap for threads. Fail-fast uses modCount.")
            .followUpQuestions(Arrays.asList("ArrayList vs LinkedList complexity?", "How does HashMap grow?", "Fail-fast vs fail-safe?", "Why does Map not extend Collection?"))
            .scenarioQuestions(Arrays.asList("Throughput dropped 10x as data grew. Where to look?", "Elements vanish from a HashSet after update. Why?"))
            .quickRevision("ArrayList=array O(1) get. HashMap=O(1) avg, treeifies at 8. HashSet backed by HashMap. TreeMap=red-black O(log n). ArrayDeque for stack/queue. ConcurrentHashMap for threads. Never List.contains in a loop."));

        topics.add(new Topic("Multithreading & Concurrency")
            .category("Core Java").difficulty("Advanced")
            .definition("Multithreading lets a JVM run several threads concurrently sharing the heap. The Java Memory Model defines which writes one thread is guaranteed to see from another.")
            .whyItMatters("Every Spring controller runs on a pool thread. Concurrency bugs are timing-dependent, pass tests, and only surface under production load.")
            .howItWorks("Thread states: NEW -> RUNNABLE -> BLOCKED/WAITING/TIMED_WAITING -> TERMINATED.\n\nThree problems: Atomicity (count++ is not atomic), Visibility (stale cached values), Ordering (reordering).\n\nTools: volatile (visibility only), Atomic classes (CAS), synchronized (mutex + visibility), ReentrantLock (tryLock, timeout).")
            .internalWorking("synchronized = monitorenter/monitorexit. HotSpot escalates: biased -> thin (CAS) -> heavyweight (OS monitor). volatile inserts memory barriers. AtomicLong uses CAS (LOCK CMPXCHG). LongAdder stripes updates across cells for high contention.")
            .syntax("ExecutorService pool = new ThreadPoolExecutor(\n    8, 16, 60L, TimeUnit.SECONDS,\n    new ArrayBlockingQueue<>(1000),\n    new ThreadPoolExecutor.CallerRunsPolicy());\n\nprivate volatile boolean running = true;\nprivate final AtomicLong counter = new AtomicLong();\n\nCompletableFuture.supplyAsync(this::fetch, pool)\n    .thenApply(this::transform)\n    .exceptionally(ex -> fallback());")
            .codeExample("// Lost update race\nclass Counter {\n    private int count = 0;\n    void increment() { count++; } // NOT atomic\n}\n// Fix: AtomicInteger, synchronized, or LongAdder\n\n// Deadlock fix: global lock ordering\nvoid transfer(Account a, Account b) {\n    Account first = a.id() < b.id() ? a : b;\n    Account second = first == a ? b : a;\n    synchronized(first) { synchronized(second) { /* transfer */ } }\n}\n\n// ThreadLocal must be cleaned on pooled threads\ntry { CTX.set(context); handle(); }\nfinally { CTX.remove(); }")
            .realWorldExample("A payment service used Executors.newFixedThreadPool with its unbounded queue. When a downstream slowed to 4s, all threads blocked, requests queued until OOM. Fix: bounded ArrayBlockingQueue + CallerRunsPolicy.")
            .advantages(Arrays.asList("Uses multiple cores", "Keeps apps responsive", "Virtual threads (Java 21) make blocking cheap"))
            .disadvantages(Arrays.asList("Races are timing-dependent", "Deadlock is easy to introduce", "Debugging changes timing"))
            .whenToUse("CPU-bound work sized to cores. I/O-bound with proper pooling. Virtual threads for high-concurrency blocking I/O on Java 21+.")
            .whenNotToUse("Don't add threads to fast sequential work. Don't use parallel streams for I/O. Don't use unbounded queues.")
            .commonMistakes(Arrays.asList("Calling run() instead of start()", "Assuming volatile makes count++ atomic", "Inconsistent lock ordering", "Unbounded thread pool queues", "Not calling ThreadLocal.remove()"))
            .interviewAnswer("Three problems: atomicity (synchronized/Atomic), visibility (volatile), ordering (happens-before). volatile != atomic so volatile count++ is broken. Executors.newFixedThreadPool has an UNBOUNDED queue - use ThreadPoolExecutor with bounded queue. Always ThreadLocal.remove() on pooled threads.")
            .followUpQuestions(Arrays.asList("volatile vs synchronized vs AtomicInteger?", "Why is count++ unsafe even with volatile?", "Explain happens-before edges", "Why is LongAdder faster than AtomicLong?"))
            .scenarioQuestions(Arrays.asList("Counter reports 99997 instead of 100000. Fix it.", "Downstream slows and pod OOMs. Trace the mechanism.", "Two threads deadlock. Thread dump shows each holding what the other wants."))
            .quickRevision("States: NEW->RUNNABLE->BLOCKED/WAITING->TERMINATED. volatile=visibility only. synchronized=mutex+visibility. Atomic=CAS. LongAdder>AtomicLong under contention. Unbounded queues cause OOM. Always ThreadLocal.remove(). Deadlock fix=global lock ordering."));

        topics.add(new Topic("Stream API & Functional Programming")
            .category("Core Java").difficulty("Advanced")
            .definition("A Stream is a lazy pipeline of aggregate operations over a source. Not a data structure - stores nothing, single-use. Pipeline: source -> intermediate ops (lazy) -> terminal op (triggers).")
            .whyItMatters("Streams replace loop-accumulate-mutate with declarative intent. They also unlock trivial parallelism for CPU-bound work.")
            .howItWorks("Intermediate (lazy): filter, map, flatMap, sorted, distinct, limit.\nTerminal (triggers): collect, toList, reduce, count, forEach, findFirst, anyMatch.\n\nLaziness + fusion: elements flow one-at-a-time, so filter().map().findFirst() stops at first match.")
            .internalWorking("Pipeline is a linked list of stages with flags (SIZED, SORTED, DISTINCT). Terminal op builds a Sink chain, pushes elements forward. count() on SIZED source skips traversal. Parallel uses ForkJoinPool.commonPool and Spliterator.trySplit.")
            .syntax("// Grouping\nMap<String, List<Employee>> byDept = staff.stream()\n    .collect(Collectors.groupingBy(Employee::dept));\n\n// Counting per group\nMap<String, Long> counts = staff.stream()\n    .collect(Collectors.groupingBy(Employee::dept, Collectors.counting()));\n\n// toMap needs merge function for duplicates\nMap<String, Employee> map = staff.stream()\n    .collect(Collectors.toMap(Employee::name, e -> e, (a, b) -> a));")
            .codeExample("// Laziness is observable\nStream<String> s = list.stream()\n    .peek(x -> System.out.println(\"peek \" + x))\n    .map(String::toUpperCase);\n// nothing runs until terminal op\nList<String> result = s.toList();\n\n// reduce for strings is O(n^2); joining is O(n)\nString slow = words.stream().reduce(\"\", (a,b) -> a+b); // BAD\nString fast = words.stream().collect(Collectors.joining()); // GOOD\n\n// Never mutate shared state from a stream\n// BROKEN in parallel:\nnames.parallelStream().forEach(results::add);\n// CORRECT:\nList<String> safe = names.parallelStream().filter(...).toList();")
            .realWorldExample("A report endpoint with 4 nested loops and 6 mutable maps (120 lines) was rewritten as one groupingBy with summarizingDouble (15 lines). Later someone added .parallelStream() for HTTP calls - it starved the common pool. Fix: CompletableFuture with dedicated executor.")
            .advantages(Arrays.asList("Declarative", "Lazy + fused = one pass", "Trivial parallelism for CPU work", "Primitive streams avoid boxing"))
            .disadvantages(Arrays.asList("Harder to debug", "Single-use", "Cannot throw checked exceptions", "Parallel shares one pool"))
            .whenToUse("Filtering, mapping, grouping collections. Primitive streams for numbers. Parallel only for CPU-bound + splittable source.")
            .whenNotToUse("Don't use for very small collections in hot paths. Never parallel streams for I/O. Never mutate shared state.")
            .commonMistakes(Arrays.asList("Reusing a stream after terminal op", "toMap without merge function", "Using parallelStream for I/O", "reduce with string concatenation (O(n^2))", "Not closing Files.lines"))
            .interviewAnswer("Stream = lazy pipeline, single-use. Intermediate ops are lazy; terminal triggers. Stages fuse into one pass. collect not reduce for mutable results. toMap needs a merge function. Parallel uses shared ForkJoinPool - CPU-bound only, never I/O.")
            .followUpQuestions(Arrays.asList("map vs flatMap?", "Why is reduce with strings O(n^2)?", "findFirst vs findAny in parallel?", "Why close Files.lines?"))
            .scenarioQuestions(Arrays.asList("toMap throws IllegalStateException in prod but not tests. Why?", "Adding .parallel() slowed everything. Explain.", "Sorting then filtering 1M records is slow. Fix."))
            .quickRevision("Lazy pipeline, single-use. filter/map/flatMap=stateless, sorted/distinct=stateful. collect>reduce for mutable. toMap needs merge fn. Parallel=ForkJoinPool.commonPool, CPU-bound only. Close Files.lines. Never mutate shared state."));

        // --- Data Types & Memory ---
        topics.add(new Topic("Data Types, Wrappers, Autoboxing & Pass-by-Value")
            .category("Core Java").difficulty("Beginner")
            .definition("Java has 8 primitives (byte, short, int, long, float, double, char, boolean) with fixed sizes. Each has a wrapper class for generics/collections. Autoboxing is compiler sugar for Integer.valueOf / intValue.")
            .whyItMatters("Integer cache (==works for -128..127 but fails above), unboxing NPE from null wrappers, and double for money are among the most common production bugs.")
            .howItWorks("IntegerCache covers -128..127 so == appears to work for small values. Java is always pass-by-value: for references, the reference is copied, not the object.")
            .syntax("Integer a = 127, b = 127; // a == b is TRUE (cached)\nInteger c = 128, d = 128; // c == d is FALSE\n// ALWAYS use .equals() for wrappers\n\n// Money: never double\nBigDecimal total = new BigDecimal(\"0.1\").add(new BigDecimal(\"0.2\")); // 0.3 exact")
            .codeExample("// Unboxing NPE\nMap<String, Integer> map = new HashMap<>();\nint val = map.get(\"missing\"); // NPE: null.intValue()\nint safe = map.getOrDefault(\"missing\", 0);\n\n// Pass-by-value proof\nvoid mutate(StringBuilder sb) {\n    sb.append(\" mutated\"); // visible - same object\n    sb = new StringBuilder(\"new\"); // NOT visible - local copy repointed\n}")
            .realWorldExample("A payments job was off by cents across millions of rows - amounts stored as double accumulated rounding errors. Fix: BigDecimal from Strings or long cents.")
            .advantages(Arrays.asList("Fixed sizes give identical behaviour everywhere", "Primitives avoid heap allocation", "Autoboxing removes conversion boilerplate"))
            .disadvantages(Arrays.asList("Integer cache makes == appear to work", "Unboxing null throws NPE", "float/double cannot represent decimals exactly"))
            .commonMistakes(Arrays.asList("Comparing Integer with == instead of equals", "Using double for money", "Assigning Map.get to int (NPE on miss)", "Integer overflow in millisecond math"))
            .interviewAnswer("8 primitives, each with a wrapper. Autoboxing = Integer.valueOf/intValue. IntegerCache -128..127 means == works for small values only - always use equals. Java is pass-by-value: the reference is copied. Never use double for money - use BigDecimal(String).")
            .followUpQuestions(Arrays.asList("Why does 127 == 127 work but 128 == 128 doesn't?", "Is Java pass-by-value or reference?", "Why is 0.1+0.2 != 0.3?"))
            .scenarioQuestions(Arrays.asList("Financial report off by cents across millions of rows. Diagnose.", "Cache hit counter works in test, breaks in production. Why?"))
            .quickRevision("8 primitives. Wrappers box onto heap. IntegerCache -128..127: always use equals. Unboxing null = NPE. Never double for money. Java = pass-by-value (reference copied). Integer overflow is silent."));

        // --- String Handling ---
        topics.add(new Topic("String Immutability, String Pool & StringBuilder")
            .category("Core Java").difficulty("Intermediate")
            .definition("String is immutable and final. Literals live in the String Constant Pool where identical literals share one object. StringBuilder is the mutable counterpart for building strings efficiently.")
            .whyItMatters("Strings dominate heap usage. Concatenation in loops is quadratic. Immutability makes String safe as a HashMap key and thread-safe.")
            .howItWorks("Literal \"java\" is pooled. new String(\"java\") creates a separate heap object. Runtime concatenation produces unpooled objects. StringBuilder starts at capacity 16, grows by doubling+2.")
            .syntax("String a = \"java\"; // pooled\nString b = new String(\"java\"); // separate heap object\na == b; // false\na.equals(b); // true\na == a.intern(); // true - intern returns pooled\n\nStringBuilder sb = new StringBuilder(256);\nsb.append(\"id=\").append(42);")
            .codeExample("// The loop trap: O(n^2)\nString slow = \"\";\nfor (int i = 0; i < 100000; i++) slow += i; // seconds, huge garbage\n\n// Fix: O(n)\nStringBuilder fast = new StringBuilder(600000);\nfor (int i = 0; i < 100000; i++) fast.append(i); // milliseconds\n\n// Java 9+: Compact Strings use byte[] for Latin-1 (half the memory)")
            .realWorldExample("A CSV export of 2M rows took 40 minutes using += concatenation. StringBuilder with setLength(0) reuse cut it to 3 minutes.")
            .advantages(Arrays.asList("Immutability = thread-safe, cached hashCode", "Pool deduplicates literals", "Compact Strings halve memory for ASCII"))
            .disadvantages(Arrays.asList("Every modification allocates a new object", "+= in loops is quadratic", "Cannot zero out for passwords"))
            .commonMistakes(Arrays.asList("Comparing with == instead of equals", "Using += in loops", "split(\".\") treats dot as regex", "Storing passwords in String"))
            .interviewAnswer("String is immutable and final. Literals are pooled (same object), new String() is not. Hash code is cached - ideal map key. += in a loop is O(n^2) because each creates a new builder + copies. Use StringBuilder. Compact Strings (Java 9) use byte[] for Latin-1.")
            .followUpQuestions(Arrays.asList("How many objects does new String(\"abc\") create?", "Why is String a good HashMap key?", "StringBuilder vs StringBuffer?"))
            .scenarioQuestions(Arrays.asList("CSV export takes 40 mins with heavy GC. Diagnose.", "split(\".\") on an IP returns empty array. Why?"))
            .quickRevision("Immutable+final. Pool for literals (== works), heap for new/runtime (== fails). Cached hashCode. += in loop = O(n^2), use StringBuilder. Compact Strings since Java 9. Passwords in char[]."));

        // --- HashMap Internals ---
        topics.add(new Topic("HashMap Internals & hashCode/equals Contract")
            .category("Core Java").difficulty("Advanced")
            .definition("HashMap stores key-value pairs in an array of buckets. Bucket index = (n-1) & hash. Java 8+ treeifies chains of 8+ entries to O(log n). Default: capacity 16, load factor 0.75.")
            .whyItMatters("HashMap is the most-used data structure and the most common source of subtle bugs: broken hashCode/equals, mutable keys, and concurrent corruption.")
            .howItWorks("put: hash key, spread (h ^ h>>>16), index = (n-1)&hash, walk chain comparing hash then equals. Resize at capacity*loadFactor, doubles. Treeify at 8 entries + table>=64.")
            .internalWorking("Java 8 resize splits bins by one bit (no rehash). Java 7 reversed chains on resize causing infinite loops under concurrency. ConcurrentHashMap: per-bin CAS + synchronized, lock-free volatile reads, no nulls.")
            .syntax("Map<String, Integer> map = new HashMap<>(1400); // presize for 1000\nmap.computeIfAbsent(key, k -> load(k)); // atomic load-once\nmap.merge(key, 1, Integer::sum); // counter\n\n// Contract:\n// equal objects MUST have equal hashCodes\n// keys MUST be effectively immutable")
            .codeExample("// Broken: equals without hashCode\nclass BadKey {\n    String id;\n    public boolean equals(Object o) { return id.equals(((BadKey)o).id); }\n    // hashCode NOT overridden -> identity hash\n}\nmap.put(new BadKey(\"a\"), \"v\");\nmap.get(new BadKey(\"a\")); // null! different bucket\n\n// Mutable key disaster\nMutableKey k = new MutableKey(\"a\");\nmap.put(k, \"v\");\nk.id = \"b\"; // hash changed\nmap.get(k); // null, yet size()==1")
            .realWorldExample("A cache had 0% hit rate and grew until OOM. Cause: key class overrode equals but not hashCode. Every get computed a different bucket. Fix: convert to a record (auto-generates both).")
            .advantages(Arrays.asList("O(1) average get/put", "Treeification bounds worst case to O(log n)", "Rich atomic methods (compute, merge)"))
            .disadvantages(Arrays.asList("Not thread-safe", "Correctness depends on hashCode/equals contract", "No ordering guarantee"))
            .commonMistakes(Arrays.asList("equals without hashCode", "Mutable keys", "Sharing HashMap across threads", "containsKey then put (not atomic)", "Not presizing for large loads"))
            .interviewAnswer("Bucket = (n-1) & (h ^ h>>>16). Capacity is power-of-two for fast modulo. Treeify at 8 in table>=64. Contract: equal objects must have equal hashCodes. Mutable keys make entries unreachable. ConcurrentHashMap: per-bin CAS, lock-free reads, no nulls. Use computeIfAbsent not get-then-put.")
            .followUpQuestions(Arrays.asList("Walk through put() step by step", "Why power-of-two capacity?", "How did resize change Java 7 to 8?", "Why does ConcurrentHashMap reject null?"))
            .scenarioQuestions(Arrays.asList("Cache has 0% hit rate and grows until OOM. Diagnose.", "CPU pinned at 100% in HashMap.get. Explain.", "containsKey returns false but size() is 1. How?"))
            .quickRevision("(n-1)&(h^h>>>16). Power-of-two capacity. Default 16, LF 0.75, resize at 12. Treeify at 8. Contract: equal->same hashCode. Mutable keys = unreachable entries. Java 8 resize splits by one bit (no infinite loop). ConcurrentHashMap: per-bin sync, no nulls."));

        // --- Exception Handling ---
        topics.add(new Topic("Exception Handling: Checked vs Unchecked & try-with-resources")
            .category("Core Java").difficulty("Intermediate")
            .definition("Throwable splits into Error (don't catch) and Exception. Under Exception: RuntimeException subtree is unchecked; everything else is checked (compiler-enforced). try-with-resources closes AutoCloseable automatically.")
            .whyItMatters("Swallowed exceptions cause silent data loss. Leaked resources from missing finally blocks cause pool exhaustion. These are among the most damaging production patterns.")
            .howItWorks("Checked = compiler forces catch or throws. Unchecked = no requirement. try-with-resources closes in reverse order, attaches close() failures as suppressed exceptions.")
            .syntax("try (Connection conn = ds.getConnection();\n     PreparedStatement ps = conn.prepareStatement(sql);\n     ResultSet rs = ps.executeQuery()) {\n    // all three closed automatically, even on exception\n}\n\n// Custom exception\npublic class OrderException extends RuntimeException {\n    public OrderException(String msg, Throwable cause) { super(msg, cause); }\n}")
            .codeExample("// Anti-patterns:\ncatch (Exception e) { }                    // swallowed - invisible failure\ncatch (Exception e) { e.printStackTrace(); } // not in your log pipeline\ncatch (Exception e) { throw new RuntimeException(e.getMessage()); } // cause LOST\n\n// Correct:\ncatch (IOException e) {\n    throw new DataLoadException(\"failed: \" + path, e); // cause preserved\n}\n\n// InterruptedException - ALWAYS restore the flag\ncatch (InterruptedException e) {\n    Thread.currentThread().interrupt();\n    throw new IllegalStateException(\"interrupted\", e);\n}")
            .realWorldExample("A nightly job silently produced incomplete output for weeks. Each row was in catch(Exception e){} - nothing logged, no metric, job exited 0. 4% of records dropped. Fix: log, count failures, fail the job above a threshold.")
            .advantages(Arrays.asList("Stack traces pinpoint failure location", "Checked exceptions document failure modes", "try-with-resources makes cleanup automatic"))
            .disadvantages(Arrays.asList("Throwing is expensive (stack trace capture)", "Checked exceptions add boilerplate", "Easy to swallow accidentally"))
            .commonMistakes(Arrays.asList("Empty catch blocks", "Using printStackTrace instead of logger", "Wrapping without preserving cause", "Catching Throwable/Error", "Returning from finally", "Swallowing InterruptedException"))
            .interviewAnswer("Throwable > Error (don't catch) + Exception. RuntimeException = unchecked, rest = checked. try-with-resources closes in reverse order, suppressed exceptions preserved. Never empty catch. Always chain the cause. Restore interrupt flag on InterruptedException. Never return from finally.")
            .followUpQuestions(Arrays.asList("Checked vs unchecked - where is the line?", "What happens if both try and finally return?", "How do suppressed exceptions work?", "ClassNotFoundException vs NoClassDefFoundError?"))
            .scenarioQuestions(Arrays.asList("Job reports success but 4% of records missing. Where to look?", "Connections leak under load despite finally. Explain.", "Hot path spends 30% CPU throwing exceptions. Fix."))
            .quickRevision("Throwable > Error + Exception > RuntimeException(unchecked) + checked. try-with-resources closes reverse order. Never empty catch. Chain the cause. Restore interrupt flag. Never return from finally. Overrides narrow, never widen checked."));

        // --- Lambdas & Functional Interfaces ---
        topics.add(new Topic("Lambda Expressions & Functional Interfaces")
            .category("Core Java").difficulty("Intermediate")
            .definition("A lambda is an anonymous function whose type is a functional interface (one abstract method). Method references (::) are shorthand. Compiled via invokedynamic, not inner classes.")
            .whyItMatters("Lambdas are the entry point to Streams, CompletableFuture, Optional, and every modern framework API. They enable behaviour parameterisation.")
            .howItWorks("Key interfaces: Function<T,R> (apply), Predicate<T> (test), Consumer<T> (accept), Supplier<T> (get), UnaryOperator<T>, BinaryOperator<T>. Non-capturing lambdas allocate nothing (cached).")
            .syntax("Function<String,Integer> len = String::length;\nPredicate<String> notBlank = s -> !s.isBlank();\nConsumer<String> print = System.out::println;\nSupplier<List<String>> factory = ArrayList::new;\n\n// Composition\nPredicate<String> valid = notBlank.and(s -> s.length() < 100);\nFunction<Integer,Integer> f = x -> x * 2;\nf.andThen(x -> x + 1).apply(5); // 11")
            .codeExample("// Before: 5 lines of ceremony\nCollections.sort(names, new Comparator<String>() {\n    public int compare(String a, String b) { return a.length() - b.length(); }\n});\n// After: one line\nnames.sort(Comparator.comparingInt(String::length));\n\n// orElse vs orElseGet - laziness matters\noptional.orElse(expensiveCall());      // ALWAYS called\noptional.orElseGet(this::expensiveCall); // only if empty\n\n// Effectively final requirement\nint total = 0;\n// list.forEach(n -> total += n); // WON'T COMPILE\nint sum = list.stream().mapToInt(Integer::intValue).sum(); // correct")
            .realWorldExample("40 single-method validator classes replaced with Map<String, Predicate<Order>> - 600 lines became 40. Rules became composable with .and()/.or().")
            .advantages(Arrays.asList("Removes anonymous class boilerplate", "Non-capturing lambdas allocate nothing", "Composable via andThen/compose/and/or", "Method references name the operation"))
            .disadvantages(Arrays.asList("Stack traces are harder to read", "Cannot throw checked exceptions", "Capturing lambdas allocate per evaluation"))
            .commonMistakes(Arrays.asList("Mutating local variables from lambda", "Using orElse with expensive call instead of orElseGet", "(a,b)->a-b comparator overflows", "Cramming complex logic into a lambda"))
            .interviewAnswer("Lambda type = functional interface (one abstract method). Compiled via invokedynamic, not inner classes. Non-capturing are cached (zero allocation). Locals must be effectively final (captured by value). this = enclosing instance. Key interfaces: Function, Predicate, Consumer, Supplier. Use comparingInt not subtraction. orElseGet for laziness.")
            .followUpQuestions(Arrays.asList("Why must captured locals be effectively final?", "What does 'this' mean inside a lambda?", "Why is (a,b)->a-b dangerous?", "Capturing vs non-capturing allocation?"))
            .scenarioQuestions(Arrays.asList("40 validator classes need to be composable. Design it.", "Debug logging allocates heavily even at INFO. Fix.", "Counter inside forEach won't compile. Rewrite."))
            .quickRevision("Functional interface = 1 abstract method. invokedynamic, not inner class. Non-capturing = cached. Locals captured by value (effectively final). this = enclosing. Function/Predicate/Consumer/Supplier + primitive variants. andThen/compose. orElseGet not orElse. comparingInt not subtraction."));

        // --- Generics ---
        topics.add(new Topic("Generics, Type Erasure & PECS")
            .category("Core Java").difficulty("Advanced")
            .definition("Generics parameterise types for compile-time safety. At runtime, type arguments are erased (replaced by bounds). Wildcards express variance: ? extends T (read), ? super T (write). PECS = Producer Extends, Consumer Super.")
            .whyItMatters("Generics make collections type-safe. Erasure explains why no new T(), no instanceof List<String>. Wildcards determine whether an API is usable or fights callers.")
            .howItWorks("Erasure: T -> Object (or bound). Casts inserted at read sites. Bridge methods preserve polymorphism. Consequences: no new T[], no static T field, no overloading on type args, no primitives.")
            .syntax("// PECS in one signature\nstatic <T> void copy(List<? super T> dest, List<? extends T> src) {\n    for (T t : src) dest.add(t);\n}\n\n// Work around erasure\npublic static <T> T fromJson(String json, Class<T> type) { }\npublic <T> T create(Supplier<T> factory) { return factory.get(); }")
            .codeExample("// Why List<String> is not List<Object>\nList<String> strings = new ArrayList<>();\n// List<Object> objects = strings; // compile error - GOOD\n// objects.add(42); // would corrupt strings\n\n// extends = read only\nList<? extends Number> nums = List.of(1, 2, 3);\nNumber n = nums.get(0); // fine\n// nums.add(4); // compile error - actual type unknown\n\n// super = write\nList<? super Integer> sink = new ArrayList<Number>();\nsink.add(1); // fine\nObject o = sink.get(0); // reads come back as Object")
            .realWorldExample("A utility taking List<Number> rejected every caller's List<Integer>. Changing to List<? extends Number> removed all workaround copies.")
            .advantages(Arrays.asList("Compile-time type safety", "Eliminates casts", "One implementation serves all types", "PECS makes APIs flexible"))
            .disadvantages(Arrays.asList("Erasure removes runtime info", "No primitives as type args", "Cannot overload on type args", "Wildcard signatures hard to read"))
            .commonMistakes(Arrays.asList("Using raw types", "Assuming List<String> is List<Object>", "Trying to add to List<? extends T>", "Attempting new T()", "Using wildcard in return type"))
            .interviewAnswer("Erasure: T replaced by bound at compile time. No new T(), no new T[], no instanceof. Generics are invariant (List<String> != List<Object>). PECS: extends = read (producer), super = write (consumer). Pass Class<T> or Supplier<T> to work around erasure. Never use raw types.")
            .followUpQuestions(Arrays.asList("Why can't you write new T()?", "Why is List<String> not List<Object>?", "Explain PECS with an example", "What are bridge methods?"))
            .scenarioQuestions(Arrays.asList("Utility taking List<Number> rejects List<Integer>. Fix.", "Generic factory fails in some subclasses. Why?", "Two overloads on List<String> vs List<Integer> won't compile. Options?"))
            .quickRevision("Erasure: T->bound, casts inserted, bridge methods. No new T(), no instanceof, no primitives, no overloading on type args. Invariant: List<String> != List<Object>. Wildcards: extends=read, super=write. PECS. Work around with Class<T> or Supplier<T>."));

        // --- I/O & NIO ---
        topics.add(new Topic("File I/O, NIO.2 & java.time API")
            .category("Core Java").difficulty("Intermediate")
            .definition("NIO.2 (Java 7) replaced File with Path+Files. java.time (Java 8) replaced Date/Calendar with immutable, thread-safe types. Both are essential modern Java APIs.")
            .whyItMatters("Leaked file handles crash services. readAllBytes on large files causes OOM. SimpleDateFormat shared across threads silently corrupts dates.")
            .howItWorks("Files.lines = lazy stream (must close). readAllBytes/readString = loads entire file (OOM risk). java.time: Instant (UTC point), ZonedDateTime (zone rules), LocalDate (no time/zone), Duration vs Period.")
            .syntax("// Stream large file - constant memory\ntry (Stream<String> lines = Files.lines(path, UTF_8)) {\n    long errors = lines.filter(l -> l.contains(\"ERROR\")).count();\n}\n\n// java.time\nInstant now = Instant.now();               // store this (UTC)\nZonedDateTime display = now.atZone(ZoneId.of(\"Asia/Kolkata\")); // display this\nDuration d = Duration.between(start, end); // time-based\nPeriod p = Period.between(birth, today);   // date-based")
            .codeExample("// File handle leak - #1 cause of 'Too many open files'\nStream<String> leaked = Files.lines(path); // never closed!\n// Fix: ALWAYS try-with-resources\n\n// OOM on large file\nString all = Files.readString(hugeFile); // 2GB file = 2GB heap\n// Fix: stream line by line\n\n// SimpleDateFormat is NOT thread-safe\nprivate static final SimpleDateFormat BAD = new SimpleDateFormat(\"yyyy-MM-dd\");\n// Fix: DateTimeFormatter is immutable and safe\nprivate static final DateTimeFormatter GOOD = DateTimeFormatter.ISO_LOCAL_DATE;")
            .realWorldExample("Import service OOMed on a 1.8GB upload using readAllLines. Files.lines in try-with-resources fixed it. Same service leaked handles from unclosed Files.list - after weeks, socket accepts failed.")
            .advantages(Arrays.asList("Files.lines streams in constant memory", "Path+Files give real exceptions (not boolean returns)", "java.time is immutable and thread-safe"))
            .disadvantages(Arrays.asList("Unclosed streams leak OS handles silently", "readAllBytes scales with file size", "Charset defaults varied pre-Java 18"))
            .commonMistakes(Arrays.asList("Not closing Files.lines/walk/list", "readAllBytes on user-supplied files", "Omitting charset", "Sharing SimpleDateFormat across threads", "Using LocalDateTime where zone matters"))
            .interviewAnswer("NIO.2: Path+Files, always try-with-resources for streams. Files.lines = constant memory; readAllBytes = entire file (OOM). Always specify charset. java.time: Instant to store (UTC), ZonedDateTime to display. DateTimeFormatter is immutable (safe); SimpleDateFormat is NOT. Duration = time-based, Period = date-based.")
            .followUpQuestions(Arrays.asList("Why must Files.lines be closed?", "readAllBytes vs BufferedReader for 2GB?", "LocalDateTime vs ZonedDateTime vs Instant?", "Why is SimpleDateFormat unsafe?"))
            .scenarioQuestions(Arrays.asList("1.8GB upload kills the pod. Fix.", "App reports 'Too many open files' after weeks. Diagnose.", "Dates are wrong one in a few thousand requests. Cause?"))
            .quickRevision("Files.lines = lazy, MUST close. readAllBytes = OOM risk. Always specify charset. java.time: Instant(store), ZonedDateTime(display), LocalDate(no time). DateTimeFormatter = safe. SimpleDateFormat = NOT safe. Duration=time, Period=date. Atomic write = temp file + ATOMIC_MOVE."));

        return topics;
    }

    // ==================== Advanced Java ====================

    private static List<Topic> buildAdvancedJava() {
        List<Topic> topics = new ArrayList<>();

        topics.add(new Topic("JDBC: Connection Pooling & Transactions")
            .category("Advanced Java").difficulty("Intermediate")
            .definition("JDBC is the standard API for relational database access. You program against java.sql interfaces while each vendor supplies a driver. PreparedStatement prevents SQL injection structurally.")
            .whyItMatters("Every persistence framework is a layer over JDBC. Leaked connections, SQL injection, and N+1 round trips are the most damaging production failures.")
            .howItWorks("Steps: obtain Connection from DataSource (pool), create PreparedStatement with ? placeholders, execute, process ResultSet, close via try-with-resources.\n\nPreparedStatement sends SQL and parameters separately - injection is structurally impossible.\n\nConnection pooling (HikariCP) keeps connections open. Opening a physical connection costs 50-200ms.")
            .syntax("try (Connection conn = dataSource.getConnection();\n     PreparedStatement ps = conn.prepareStatement(sql)) {\n    ps.setString(1, \"ACTIVE\");\n    try (ResultSet rs = ps.executeQuery()) {\n        while (rs.next()) process(rs);\n    }\n}\n\n// Batch insert\nfor (Order o : orders) {\n    ps.setLong(1, o.id());\n    ps.addBatch();\n}\nps.executeBatch();")
            .codeExample("// SQL INJECTION - the #1 mistake\nString unsafe = \"SELECT * FROM users WHERE email = '\" + email + \"'\";\n// email = \"x' OR '1'='1\" returns everything\n\n// SAFE\nps.setString(1, email); // value never parsed as SQL\n\n// Connection leak\nConnection conn = ds.getConnection();\nps.executeQuery(); // exception here = conn never closed\n// Fix: ALWAYS try-with-resources")
            .realWorldExample("Service returned 500s: 'connection not available, timed out after 30s'. Pool was 50 connections, DB idle. Cause: a reporting endpoint leaked connections on exceptions. After ~50 failures the pool drained. Fix: try-with-resources + leakDetectionThreshold.")
            .advantages(Arrays.asList("Vendor-neutral API", "PreparedStatement = injection-safe + plan reuse", "Direct SQL control", "Batching gives 10x+ throughput"))
            .disadvantages(Arrays.asList("Verbose", "Manual ResultSet mapping", "Leaked connections are silent until pool exhaustion"))
            .commonMistakes(Arrays.asList("Concatenating user input into SQL", "Closing resources outside try-with-resources", "Per-row execution instead of batching", "Oversizing the connection pool"))
            .interviewAnswer("JDBC: vendor-neutral API. PreparedStatement sends SQL and params separately - injection impossible. ALWAYS try-with-resources. Batch for bulk writes (10x improvement). Small pool (core_count*2). HikariCP with leakDetectionThreshold.")
            .followUpQuestions(Arrays.asList("How does PreparedStatement prevent injection?", "Why is a smaller pool often faster?", "Explain isolation levels"))
            .scenarioQuestions(Arrays.asList("All endpoints hang with 'connection not available'. Diagnose.", "Inserting 200k rows takes 25 minutes. Speed it up."))
            .quickRevision("PreparedStatement=injection-safe. ALWAYS try-with-resources. Batch for bulk. Small pool (HikariCP). Isolation: READ_UNCOMMITTED/COMMITTED/REPEATABLE_READ/SERIALIZABLE."));

        topics.add(new Topic("Spring Core: IoC, DI, AOP & Bean Scopes")
            .category("Advanced Java").difficulty("Advanced")
            .definition("Spring's core is an IoC container. Classes declare dependencies; the container instantiates, injects, and manages lifecycle. DI is the mechanism; AOP handles cross-cutting concerns via proxies.")
            .whyItMatters("Everything in Spring (Boot, Data, Security) is built on the container and AOP proxies. Most confusing Spring behaviour has a container explanation.")
            .howItWorks("Three injection styles: Constructor (recommended, final fields), Setter (optional deps), Field (discouraged).\n\nBean scopes: singleton (default - MUST be stateless), prototype, request, session.\n\nAOP: proxy-based. JDK dynamic proxy if interface, CGLIB subclass otherwise. Advice types: @Before, @After, @Around.")
            .internalWorking("Self-invocation problem: this.method() bypasses the proxy, so @Transactional/@Cacheable/@Async silently do nothing on internal calls. Also ignored on private/final/static methods. Fix: move annotated method to another bean.\n\nCircular deps with constructor injection fail fast (good). Field injection hides them. Boot 2.6+ rejects cycles by default.")
            .syntax("@Service\npublic class OrderService {\n    private final OrderRepository repo;\n    public OrderService(OrderRepository repo) { this.repo = repo; }\n}\n\n// Strategy pattern with DI\npublic PaymentRouter(List<PaymentGateway> all) { }\n\n@Bean @ConditionalOnMissingBean\npublic PaymentGateway defaultGateway() { }")
            .codeExample("// Self-invocation trap - #1 Spring bug\n@Service\npublic class BrokenTx {\n    public void process(List<Order> orders) {\n        for (Order o : orders) saveOne(o); // this.saveOne = NO PROXY\n    }\n    @Transactional\n    public void saveOne(Order o) { repo.save(o); } // NOT transactional!\n}\n// Fix: move saveOne to a separate bean\n\n// Singleton must be stateless\n@Service\npublic class Bug {\n    private String user; // SHARED across all threads!\n}")
            .realWorldExample("A batch job committed nothing. The loop and @Transactional method were the same bean - internal this.saveRow() bypassed the proxy, so no per-row transaction existed. Moving it to another bean fixed it.")
            .advantages(Arrays.asList("Decouples classes from implementations", "Testable without container", "AOP removes cross-cutting boilerplate", "Constructor injection = immutable objects"))
            .disadvantages(Arrays.asList("Proxy AOP fails on self-invocation", "Startup cost grows with bean count", "Deep proxy stack traces"))
            .commonMistakes(Arrays.asList("Calling @Transactional internally via this", "@Transactional on private/final methods", "Mutable state in singleton beans", "Field injection hiding circular deps"))
            .interviewAnswer("ALWAYS constructor injection (final, testable). Singleton = MUST be stateless. AOP is proxy-based: self-invocation via this bypasses the proxy, so @Transactional does nothing on internal calls. Fix: separate bean. Also ignored on private/final/static. Boot 2.6+ rejects circular deps.")
            .followUpQuestions(Arrays.asList("Why does @Transactional fail on internal calls?", "Constructor vs field injection?", "How do you get a new prototype inside a singleton?"))
            .scenarioQuestions(Arrays.asList("Batch commits nothing despite @Transactional. Diagnose.", "@Cacheable is ignored - every call hits DB. Why?", "Audit logs show wrong user under load. Cause?"))
            .quickRevision("Constructor injection (final, testable). Singleton=stateless. AOP=proxy: self-invocation bypasses it. @Transactional/@Cacheable/@Async ignored on this.method(), private, final, static. Fix=move to another bean."));

        topics.add(new Topic("Spring Boot: Auto-Configuration & Actuator")
            .category("Advanced Java").difficulty("Advanced")
            .definition("Spring Boot adds auto-configuration, starters, embedded server, and Actuator. @ConditionalOnMissingBean means auto-config backs off when you define your own bean.")
            .whyItMatters("Boot is the default way Java backends are built. Understanding auto-config explains why beans appear or disappear.")
            .howItWorks("@SpringBootApplication = @Configuration + @ComponentScan + @EnableAutoConfiguration.\n\nConfig precedence (highest wins): CLI args > env vars > system props > application-{profile}.yml > application.yml.\n\nActuator: /actuator/health/liveness (restart me), /actuator/health/readiness (stop traffic).")
            .syntax("# application.yml\nspring:\n  datasource:\n    url: ${DB_URL}\n    hikari:\n      maximum-pool-size: 10\n  jpa:\n    open-in-view: false        # IMPORTANT\n    hibernate:\n      ddl-auto: validate       # NEVER update in prod\n\nmanagement:\n  endpoint:\n    health:\n      probes:\n        enabled: true")
            .codeExample("// Your bean disables auto-config\n@Bean\npublic DataSource myDataSource() { } // Boot's backs off\n\n// Typed config - fails fast at startup\n@ConfigurationProperties(prefix = \"app.payment\")\npublic record PaymentProps(\n    @NotBlank String apiUrl,\n    Duration timeout) { }\n\n// Diagnose: run with --debug for conditions report")
            .realWorldExample("Production silently used H2 because no DB_URL env var was set and the property had a default. Fix: remove the default so startup fails fast. Also: pointing both probes at /actuator/health caused restart storms during a partner outage.")
            .advantages(Arrays.asList("Auto-config removes boilerplate", "Starters manage versions", "Embedded server = single jar", "Actuator for observability"))
            .disadvantages(Arrays.asList("Implicit behaviour hard to trace", "open-in-view default is dangerous", "Fat jars are large"))
            .commonMistakes(Arrays.asList("Components outside scan package", "open-in-view=true hiding N+1", "ddl-auto=update in production", "Same endpoint for liveness and readiness", "Exposing all actuator endpoints"))
            .interviewAnswer("@SpringBootApplication = scan + auto-config. @ConditionalOnMissingBean = your bean wins. Config: CLI > env > props > yml. Always set open-in-view=false, ddl-auto=validate. Separate liveness/readiness probes or a dependency blip restarts every pod.")
            .followUpQuestions(Arrays.asList("How does auto-configuration work?", "What is @ConditionalOnMissingBean?", "Why separate liveness from readiness?"))
            .scenarioQuestions(Arrays.asList("Prod silently used H2. How?", "Brief partner outage restarted every pod. Explain.", "Queries explode under load but not in dev. What default?"))
            .quickRevision("@SpringBootApplication = scan + auto-config. ConditionalOnMissingBean = your bean disables default. open-in-view=false. ddl-auto=validate. Separate liveness/readiness probes. --debug shows conditions report."));

        topics.add(new Topic("Hibernate/JPA: N+1, Lazy Loading & Transactions")
            .category("Advanced Java").difficulty("Advanced")
            .definition("JPA is the ORM spec, Hibernate the implementation. The persistence context tracks managed entities, dirty-checks them, and flushes SQL at transaction commit.")
            .whyItMatters("N+1 selects are the most common cause of slow services. LazyInitializationException vs open-in-view is a genuinely awkward trade-off.")
            .howItWorks("Entity states: Transient -> Managed (dirty-checked) -> Detached -> Removed.\n\nFlush: at commit, before relevant queries, or explicitly.\n\nDefaults: @ManyToOne=EAGER (wrong), @OneToMany=LAZY.\n\n@Version = optimistic locking.")
            .syntax("@Entity\npublic class Order {\n    @Id @GeneratedValue(strategy = IDENTITY)\n    private Long id;\n    @Version private Long version;\n    @ManyToOne(fetch = LAZY) private Customer customer;\n    @OneToMany(mappedBy = \"order\", cascade = ALL) private List<OrderLine> lines;\n}\n\n// Fix N+1\n@Query(\"SELECT o FROM Order o JOIN FETCH o.customer WHERE o.status = :st\")\nList<Order> findWithCustomer(@Param(\"st\") Status st);")
            .codeExample("// N+1: 1 query for orders + 1 per order for customer = 101\nList<Order> orders = repo.findByStatus(PAID);\nfor (Order o : orders) o.getCustomer().getName(); // +N queries\n\n// Fix: JOIN FETCH, @EntityGraph, or batch_fetch_size\n\n// You do NOT call save on a managed entity\n@Transactional\npublic void rename(Long id, String name) {\n    Order o = repo.findById(id).orElseThrow();\n    o.setReference(name); // dirty checking generates UPDATE at flush\n}\n\n// @Enumerated(ORDINAL) is a data corruption trap\n@Enumerated(STRING) private Status status; // ALWAYS STRING")
            .realWorldExample("Endpoint: 80ms locally, 6s in production. Query log showed 1200 statements for one request. Dev had 20 rows, prod had 400. @EntityGraph + batch_fetch_size fixed it.")
            .advantages(Arrays.asList("Eliminates CRUD SQL", "Dirty checking = no explicit save", "Persistence context deduplicates", "@Version gives lost-update protection"))
            .disadvantages(Arrays.asList("N+1 is the default failure mode", "Generated SQL can be suboptimal", "LazyInitializationException vs open-in-view"))
            .commonMistakes(Arrays.asList("Leaving @ManyToOne at EAGER", "Not detecting N+1 on small test data", "JOIN FETCH + Pageable = in-memory pagination", "Omitting @Version", "equals/hashCode on generated id"))
            .interviewAnswer("N+1: loading 100 orders then touching customer per order = 101 queries. Fixes: JOIN FETCH, @EntityGraph, batch_fetch_size, or DTO projection. Set open-in-view=false. @Version for optimistic locking. @Enumerated(STRING) always. Bulk updates via @Modifying JPQL, not loading entities.")
            .followUpQuestions(Arrays.asList("What is N+1 and four ways to fix it?", "Why does JOIN FETCH break pagination?", "persist vs merge?", "Why @Enumerated(ORDINAL) is dangerous?"))
            .scenarioQuestions(Arrays.asList("80ms locally, 6s in production. Diagnose.", "Inventory counts drift with no errors. Explain.", "Nightly archive OOMs on 1.4M rows. Fix."))
            .quickRevision("N+1 fixes: JOIN FETCH, @EntityGraph, batch_fetch_size, DTO projection. open-in-view=false. @Version for optimistic locking. @Enumerated(STRING). Managed entities don't need save(). Bulk = @Modifying JPQL."));

        topics.add(new Topic("Microservices: Resilience & Distributed Data")
            .category("Advanced Java").difficulty("Advanced")
            .definition("Microservices decompose an app into independently deployable services. The defining trade: a method call becomes a network call which can be slow, fail, or succeed without you knowing.")
            .whyItMatters("Without timeouts, retries, circuit breakers, and idempotency, one slow dependency cascades into a full outage.")
            .howItWorks("Resilience stack: timeout + circuit breaker (CLOSED->OPEN->HALF_OPEN) + bulkhead + fallback.\n\nRetries must be bounded with exponential backoff + jitter, and ONLY on idempotent operations.\n\nData: no distributed transactions - use Saga with compensating actions. Transactional outbox for reliable event publishing.")
            .syntax("@CircuitBreaker(name = \"payment\", fallbackMethod = \"fallback\")\n@Retry(name = \"payment\")\n@Bulkhead(name = \"payment\")\npublic PaymentResponse charge(Request req) { }\n\n# resilience4j config\nresilience4j.circuitbreaker.instances.payment:\n  failure-rate-threshold: 50\n  wait-duration-in-open-state: 30s\nresilience4j.retry.instances.payment:\n  max-attempts: 3\n  enable-exponential-backoff: true")
            .codeExample("// Cascading failure: no timeout on slow dependency\n// All 200 threads block -> health checks fail -> pod restarts\n// Fix: timeout + circuit breaker + bulkhead\n\n// Retry without idempotency = double charge\n// ALWAYS pass an idempotency key\nclient.charge(req, \"order-\" + order.getId());\n\n// Dual-write problem: DB + Kafka are two systems\n// Crash between them loses the event\n// Fix: transactional outbox\n@Transactional\nvoid place(Order o) {\n    orderRepo.save(o);\n    outboxRepo.save(new OutboxEvent(\"orders\", toJson(o)));\n}")
            .realWorldExample("Checkout went down because a non-essential recommendations service was slow (no timeout). All 200 Tomcat threads blocked on it. Fix: 500ms timeout + circuit breaker + semaphore bulkhead capping at 20 concurrent calls. Endpoint now returns empty sidebar instead of hanging.")
            .advantages(Arrays.asList("Independent deployment", "Independent scaling", "Fault isolation with proper boundaries"))
            .disadvantages(Arrays.asList("Distributed complexity", "No distributed transactions", "Requires tracing infrastructure"))
            .commonMistakes(Arrays.asList("No timeouts on outbound calls", "Retrying non-idempotent operations", "Unbounded retries", "Dual-write without outbox", "Assuming exactly-once delivery"))
            .interviewAnswer("Cascading failure: no timeout -> threads block -> health fails -> restart. Fix: timeout + circuit breaker (fail fast) + bulkhead (isolate thread budget) + fallback. Retries need idempotency keys. No distributed transactions - use Saga + compensating actions. Transactional outbox for reliable events. At-least-once + idempotent consumers.")
            .followUpQuestions(Arrays.asList("Explain circuit breaker states", "Why retries need idempotency", "What is the transactional outbox?", "Saga orchestration vs choreography?"))
            .scenarioQuestions(Arrays.asList("Optional feature took down checkout. Explain.", "Customers charged twice after network blip. Fix.", "Order exists but fulfilment never got the event. What pattern?"))
            .quickRevision("Timeout + circuit breaker + bulkhead + fallback. Retry ONLY idempotent ops with backoff+jitter. No distributed tx -> Saga + compensating. Dual-write -> transactional outbox. At-least-once + idempotent consumers."));

        // --- Servlets ---
        topics.add(new Topic("Servlets: Lifecycle, Filters & Session Management")
            .category("Advanced Java").difficulty("Intermediate")
            .definition("A Servlet handles HTTP requests inside a container (Tomcat). ONE instance shared by ALL threads. HttpServlet dispatches to doGet/doPost. Filters intercept requests for cross-cutting concerns.")
            .whyItMatters("Spring MVC is one servlet (DispatcherServlet). Instance fields in a servlet = shared state across all concurrent requests. Thread pool exhaustion from slow downstream = total app failure.")
            .howItWorks("Lifecycle: init (once) -> service/doGet/doPost (per request, pool thread) -> destroy (once). Single instance, many threads. Filters chain around the servlet. Scopes: request < session < application.")
            .syntax("@WebServlet(\"/api/orders\")\npublic class OrderServlet extends HttpServlet {\n    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {\n        String id = req.getParameter(\"id\"); // LOCAL - safe\n        resp.setContentType(\"application/json\");\n        resp.getWriter().write(toJson(service.find(id)));\n    }\n}\n\n@WebFilter(\"/api/*\")\npublic class AuthFilter implements Filter {\n    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) {\n        if (unauthorized) { ((HttpServletResponse)rs).sendError(401); return; }\n        chain.doFilter(rq, rs); // continue\n    }\n}")
            .codeExample("// THE defining servlet bug - instance field shared across ALL threads\npublic class BrokenServlet extends HttpServlet {\n    private String currentUser; // SHARED!\n    protected void doGet(HttpServletRequest req, ...) {\n        currentUser = req.getParameter(\"user\"); // thread A overwrites B\n        resp.getWriter().write(\"Hello \" + currentUser); // wrong user!\n    }\n}\n\n// Thread pool exhaustion\n// No timeout -> downstream hangs -> all 200 threads block -> app dead\n// Fix: always set connect + read timeouts on outbound calls")
            .realWorldExample("A servlet stored username in an instance field. Under load, users saw each other's data. Fix: use local variables or request attributes, never instance fields.")
            .advantages(Arrays.asList("Container handles sockets, threading, parsing", "Filters give clean cross-cutting", "One instance = low memory"))
            .disadvantages(Arrays.asList("Instance fields = concurrency hazard", "One thread per request limits concurrency", "Raw servlet code is verbose"))
            .commonMistakes(Arrays.asList("Storing per-request data in instance fields", "No timeouts on outbound calls", "Not rotating session id on login", "Setting encoding after getParameter"))
            .interviewAnswer("ONE instance, ALL threads - instance fields are shared mutable state. Tomcat ~200 threads, one per blocking request: always set timeouts. Filters = cross-cutting (auth, CORS). forward = server-side (URL unchanged), redirect = 302 (new request). Rotate session id on login.")
            .followUpQuestions(Arrays.asList("How many servlet instances does the container create?", "forward vs sendRedirect?", "What causes thread pool exhaustion?"))
            .scenarioQuestions(Arrays.asList("Users see each other's data under load. Cause?", "App hangs but CPU is idle. Explain."))
            .quickRevision("ONE instance, ALL threads. Instance fields = SHARED. Lifecycle: init->service->destroy. ~200 threads, one per request. Always timeout outbound calls. Filters: chain.doFilter continues. forward=server-side, redirect=302. Rotate session on login."));

        // --- JSP ---
        topics.add(new Topic("JSP: EL, JSTL & MVC Pattern")
            .category("Advanced Java").difficulty("Beginner")
            .definition("JSP is translated into a servlet. EL (${...}) accesses scoped attributes. JSTL provides tags for logic (c:if, c:forEach). The MVC pattern keeps logic in controllers, views in JSP.")
            .whyItMatters("Still widespread in legacy systems. Key lesson: bare EL doesn't escape HTML (XSS vulnerability). Scriptlets put untestable logic in views.")
            .howItWorks("Translation: JSP -> Java servlet -> compiled class. Declarations <%! %> become instance fields (shared = race condition). EL searches page > request > session > application scope.")
            .syntax("<%@ taglib prefix=\"c\" uri=\"http://java.sun.com/jsp/jstl/core\" %>\n\n${user.name}                    <%-- calls getName() --%>\n<c:out value=\"${param.input}\" /> <%-- ESCAPES HTML - prevents XSS --%>\n\n<c:forEach items=\"${orders}\" var=\"o\">\n    <tr><td>${o.id}</td><td><fmt:formatNumber value=\"${o.total}\" type=\"currency\"/></td></tr>\n</c:forEach>\n\n<c:if test=\"${not empty orders}\">...</c:if>")
            .codeExample("// MVC pattern:\n// Controller servlet:\nList<Order> orders = service.findRecent();\nreq.setAttribute(\"orders\", orders);\nreq.getRequestDispatcher(\"/WEB-INF/views/orders.jsp\").forward(req, resp);\n\n// JSP (view only, zero Java):\n<c:forEach items=\"${orders}\" var=\"o\">...</c:forEach>\n\n// XSS vulnerability:\n${param.comment}              <%-- <script>...</script> EXECUTES --%>\n<c:out value=\"${param.comment}\"/> <%-- SAFE: escaped --%>\n\n// Declarations are DANGEROUS:\n<%! int count = 0; %> <%-- INSTANCE FIELD = race condition --%>")
            .realWorldExample("A comments page executed submitted script tags because it used bare EL. Replacing with <c:out> across the codebase fixed the stored XSS. JSPs moved under WEB-INF to prevent direct access bypassing auth filters.")
            .advantages(Arrays.asList("Natural for HTML-heavy pages", "EL+JSTL = no Java needed in views", "c:out escapes by default"))
            .disadvantages(Arrays.asList("Scriptlets = untestable logic in views", "Bare EL = XSS", "Legacy - prefer Thymeleaf"))
            .commonMistakes(Arrays.asList("Using bare EL for user input (XSS)", "Declarations creating shared state", "Business logic in scriptlets", "JSPs accessible directly (bypassing auth)"))
            .interviewAnswer("JSP = translated to servlet. Declarations = instance fields (race condition). EL searches scope chain, is null-safe but does NOT escape - use <c:out> for XSS prevention. MVC: controller forwards to JSP under WEB-INF. Legacy - use Thymeleaf for new projects.")
            .followUpQuestions(Arrays.asList("How is a JSP related to a servlet?", "Why is bare EL an XSS risk?", "include directive vs jsp:include?"))
            .scenarioQuestions(Arrays.asList("Page counter reports wrong totals under load. Explain.", "Comments page executes script tags. Fix."))
            .quickRevision("JSP = translated to servlet. <%! %> = instance field (shared/unsafe). EL = no escape (use c:out). JSTL: c:if, c:forEach, c:out. MVC: controller -> setAttribute -> forward -> JSP. Keep JSPs under WEB-INF. Legacy."));

        // --- Spring MVC/REST ---
        topics.add(new Topic("Spring MVC & REST APIs")
            .category("Advanced Java").difficulty("Advanced")
            .definition("Spring MVC is a front-controller framework (DispatcherServlet). @RestController serializes return values to JSON. Strict DTO boundaries prevent mass assignment and N+1 during serialization.")
            .whyItMatters("This layer defines your public contract. Returning entities = schema coupling + mass assignment + N+1. Correct status codes make APIs usable and monitorable.")
            .howItWorks("Flow: DispatcherServlet -> HandlerMapping -> HandlerAdapter -> ArgumentResolvers -> HttpMessageConverter (Jackson). @ControllerAdvice centralizes error handling.")
            .syntax("@RestController\n@RequestMapping(\"/api/v1/orders\")\npublic class OrderController {\n    @GetMapping(\"/{id}\")\n    public OrderResponse get(@PathVariable UUID id) { return service.find(id); }\n\n    @PostMapping\n    @ResponseStatus(CREATED)\n    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {\n        OrderResponse r = service.create(req);\n        return ResponseEntity.created(URI.create(\"/api/v1/orders/\" + r.id())).body(r);\n    }\n}\n\n// DTOs: records are ideal\npublic record CreateOrderRequest(@NotBlank String customerId, @NotEmpty List<LineItem> items) {}")
            .codeExample("// NEVER return entities - mass assignment + N+1 + schema coupling\n@PostMapping\npublic Order bad(@RequestBody Order order) { return repo.save(order); }\n// Client sends {\"role\":\"ADMIN\"} and Jackson sets it!\n\n// Global error handling\n@RestControllerAdvice\npublic class ErrorHandler {\n    @ExceptionHandler(MethodArgumentNotValidException.class)\n    public ProblemDetail onValidation(MethodArgumentNotValidException e) {\n        ProblemDetail pd = ProblemDetail.forStatus(400);\n        pd.setProperty(\"errors\", e.getFieldErrors().stream()\n            .collect(toMap(FieldError::getField, FieldError::getDefaultMessage)));\n        return pd;\n    }\n}")
            .realWorldExample("An API returned entities directly. A pen tester set role=ADMIN in the payload. Adding a database column broke the mobile client's contract. N+1 during serialization caused 4s responses. Fix: separate request/response records.")
            .advantages(Arrays.asList("Annotation-driven, minimal boilerplate", "Bean Validation integrates with @Valid", "@ControllerAdvice centralizes errors", "MockMvc for fast tests"))
            .disadvantages(Arrays.asList("One thread per request", "Easy to leak entities", "Annotation-heavy = implicit flow"))
            .commonMistakes(Arrays.asList("Returning entities (mass assignment + N+1)", "200 for all outcomes including errors", "Forgetting @Valid", "No pagination on collections", "Leaking stack traces"))
            .interviewAnswer("DispatcherServlet front controller. ALWAYS use DTOs - entities expose schema, enable mass assignment, trigger N+1. @Valid + @ControllerAdvice + ProblemDetail for errors. Status codes: 201+Location on create, 204 on delete, 400 for validation, 404 for not-found. Test with @WebMvcTest.")
            .followUpQuestions(Arrays.asList("Why never return entities?", "What is mass assignment?", "@PathVariable vs @RequestParam?", "How does @ControllerAdvice work?"))
            .scenarioQuestions(Arrays.asList("Tester sets role=ADMIN in payload. Explain.", "Adding a column breaks the mobile client. Design error?", "List endpoint issues 101 queries for 100 records. Diagnose."))
            .quickRevision("DispatcherServlet -> HandlerMapping -> Adapter -> Converter. ALWAYS DTOs (never entities). @Valid for input. @ControllerAdvice + ProblemDetail for errors. 201+Location, 204, 400, 404, 409. Never 200 for errors. @WebMvcTest for testing."));

        // --- Spring Data JPA & Transactions ---
        topics.add(new Topic("Spring Data JPA & Transaction Management")
            .category("Advanced Java").difficulty("Advanced")
            .definition("Spring Data JPA generates repository implementations from interfaces. @Transactional provides declarative transaction management via AOP proxy. Default: checked exceptions COMMIT, unchecked ROLLBACK.")
            .whyItMatters("The rollback default causes silent data loss with checked exceptions. Self-invocation bypasses the proxy. readOnly=true is a genuine optimization, not just documentation.")
            .howItWorks("Repositories: derived queries from method names, @Query for JPQL/native, Specifications for dynamic. @Transactional: REQUIRED (default), REQUIRES_NEW (independent commit). Page adds a COUNT query; Slice does not.")
            .syntax("public interface OrderRepository extends JpaRepository<Order, Long> {\n    Optional<Order> findByReference(String ref);\n    Page<Order> findByStatus(Status status, Pageable pageable);\n\n    @Query(\"SELECT o FROM Order o JOIN FETCH o.customer WHERE o.status = :st\")\n    List<Order> findWithCustomer(@Param(\"st\") Status st);\n\n    @Modifying(clearAutomatically = true)\n    @Query(\"UPDATE Order o SET o.status = :s WHERE o.createdAt < :cutoff\")\n    int archiveOld(@Param(\"cutoff\") Instant cutoff, @Param(\"s\") Status s);\n}\n\n@Service\npublic class OrderService {\n    @Transactional(readOnly = true)\n    public OrderResponse find(Long id) { }\n\n    @Transactional(rollbackFor = Exception.class)\n    public void transfer(...) throws PaymentException { }\n}")
            .codeExample("// Silent data loss: checked exception COMMITS by default\n@Transactional\npublic void transfer(Long from, Long to, BigDecimal amt) throws InsufficientFundsException {\n    debit(from, amt);\n    throw new InsufficientFundsException(); // CHECKED -> COMMITS the debit!\n}\n// Fix: @Transactional(rollbackFor = Exception.class)\n\n// Catching inside defeats rollback\n@Transactional\npublic void process(Order o) {\n    try { repo.save(o); gateway.charge(o); }\n    catch (Exception e) { log.error(\"failed\", e); } // swallowed -> COMMITS\n}\n\n// Self-invocation = no transaction\npublic void importAll(List<Order> orders) {\n    for (Order o : orders) saveOne(o); // this.saveOne -> NO PROXY\n}\n@Transactional public void saveOne(Order o) { } // not transactional here!")
            .realWorldExample("A transfer debited one account and threw a checked exception - the debit committed, money disappeared. The bug existed for months. Fix: rollbackFor=Exception.class. A batch importer committed nothing because this.saveOne() bypassed the proxy.")
            .advantages(Arrays.asList("Eliminates DAO boilerplate", "Derived queries are self-documenting", "readOnly=true skips dirty checking", "Paging built in"))
            .disadvantages(Arrays.asList("Checked exceptions commit by default", "Self-invocation bypasses proxy", "Page always runs COUNT (use Slice)", "findAll can load entire table"))
            .commonMistakes(Arrays.asList("Assuming checked exceptions roll back", "Catching exceptions inside @Transactional", "Self-invocation via this", "@Transactional on private/final methods", "Page where Slice suffices", "findAll on growing tables"))
            .interviewAnswer("ROLLBACK DEFAULT: unchecked rolls back, CHECKED COMMITS - use rollbackFor=Exception.class. Catching inside also commits. Proxy-based: this.method() = NO transaction. readOnly=true skips dirty checking. Page adds COUNT (expensive) - use Slice. @Transactional on SERVICE layer, never controller.")
            .followUpQuestions(Arrays.asList("What is the default rollback behaviour?", "Why does self-invocation bypass @Transactional?", "REQUIRED vs REQUIRES_NEW?", "Page vs Slice?"))
            .scenarioQuestions(Arrays.asList("Transfer debits but never credits on failure. Diagnose.", "Batch commits nothing despite @Transactional. Explain.", "Paginated admin page times out on 50M rows. Cause?"))
            .quickRevision("Checked exceptions COMMIT by default! Use rollbackFor=Exception.class. Catching = commits. Self-invocation via this = NO proxy/transaction. REQUIRES_NEW = independent commit (uses extra connection). readOnly=true = no dirty checking. Page adds COUNT, Slice doesn't. Never findAll on growing tables."));

        // --- Spring Security ---
        topics.add(new Topic("Spring Security: JWT, Filters & Method Security")
            .category("Advanced Java").difficulty("Advanced")
            .definition("Spring Security is a filter chain handling authentication (who) and authorization (what). Rules are first-match-wins. JWT is signed not encrypted. @PreAuthorize is proxy-based (same self-invocation issue).")
            .whyItMatters("Rule ordering is a security property: a broad permitAll before a specific role rule silently opens the endpoint. JWT without signature verification = trivially forgeable tokens.")
            .howItWorks("Filter chain: context -> headers -> CORS -> CSRF -> auth filters -> AuthorizationFilter. Password: BCrypt/Argon2 (slow+salted). JWT: verify signature + exp + iss + aud, PIN the algorithm. CSRF needed for cookies only, not bearer tokens.")
            .syntax("@Configuration @EnableWebSecurity @EnableMethodSecurity\npublic class SecurityConfig {\n    @Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {\n        return http\n            .csrf(c -> c.disable()) // OK for stateless bearer tokens\n            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))\n            .authorizeHttpRequests(auth -> auth\n                .requestMatchers(\"/api/admin/**\").hasRole(\"ADMIN\") // specific FIRST\n                .requestMatchers(\"/api/auth/**\").permitAll()\n                .anyRequest().authenticated()) // ALWAYS LAST\n            .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))\n            .build();\n    }\n    @Bean PasswordEncoder encoder() {\n        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // {bcrypt}\n    }\n}")
            .codeExample("// BROKEN: broad rule first = admin is PUBLIC\n.authorizeHttpRequests(auth -> auth\n    .requestMatchers(\"/api/**\").permitAll() // matches /api/admin too!\n    .requestMatchers(\"/api/admin/**\").hasRole(\"ADMIN\") // never evaluated\n)\n\n// JWT without signature verification = forgeable\nString payload = decode(token.split(\".\")[1]);\nif (payload.role == \"ADMIN\") { } // attacker edits payload freely!\n// CORRECT: verify signature, exp, iss, aud, PIN algorithm\n\n// IDOR: authentication != authorization\n@GetMapping(\"/orders/{id}\")\npublic Order broken(@PathVariable Long id) { return repo.findById(id); }\n// ANY authenticated user reads ANY order. Fix: check ownership")
            .realWorldExample("Admin endpoints were public for 3 months because permitAll(\"/api/**\") was listed before hasRole(\"ADMIN\") for /api/admin/**. Found only during a pen test. A JWT filter decoded without verifying - forging admin took editing one JSON field.")
            .advantages(Arrays.asList("Secure defaults (CSRF, headers, session fixation)", "Pluggable (form, JWT, OAuth2, LDAP)", "DelegatingPasswordEncoder supports migration", "Method security for fine-grained rules"))
            .disadvantages(Arrays.asList("Rule ordering is a silent security hazard", "Steep learning curve", "ThreadLocal context doesn't cross @Async threads", "@PreAuthorize shares self-invocation issue"))
            .commonMistakes(Arrays.asList("Broad permitAll before specific role rules", "Hashing with MD5/SHA instead of BCrypt", "Decoding JWT without verifying signature", "Trusting alg header (none-algorithm attack)", "Disabling CSRF with session cookies", "IDOR: not checking resource ownership"))
            .interviewAnswer("Filter chain, FIRST-MATCH-WINS: specific rules first, anyRequest() LAST. Passwords: BCrypt (slow+salted), never MD5. JWT: signed NOT encrypted, verify signature+exp+iss+aud, PIN algorithm. CSRF needed for cookies only. SecurityContext is ThreadLocal - doesn't cross @Async. @PreAuthorize is proxy-based (self-invocation bypasses).")
            .followUpQuestions(Arrays.asList("Why does rule order matter?", "Why BCrypt over SHA-256?", "What must you validate on a JWT?", "When is CSRF needed?", "Why is SecurityContext missing in @Async?"))
            .scenarioQuestions(Arrays.asList("Admin endpoints public for 3 months. Configuration error?", "Attacker forges admin token by editing one field. What's missing?", "Any user reads any order by changing the id. Fix."))
            .quickRevision("Filter chain: FIRST MATCH WINS. Specific rules first, anyRequest() LAST. BCrypt/Argon2, never MD5. JWT: signed not encrypted, verify sig+exp+iss+aud, PIN algorithm. None-alg attack if you trust the header. CSRF for cookies only. SecurityContext = ThreadLocal, doesn't cross threads. @PreAuthorize = proxy-based."));

        return topics;
    }
}
