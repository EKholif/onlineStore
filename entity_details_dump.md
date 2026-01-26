# All Entities Detail Report

## Entity: AuditLog
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\AuditLog.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `String` | `tenantId` | @Column(nullable = false) |
| `String` | `module` | @Column(nullable = false) |
| `String` | `action` | @Column(nullable = false) |
| `String` | `userEmail` | @Column(nullable = false) |
| `String` | `details` | @Column(length = 1000) |
| `LocalDateTime` | `timestamp` | - |

### Methods
- **getId**(``) -> `Long`
- **setId**(`Long id`) -> `void`
- **getTenantId**(``) -> `String`
- **setTenantId**(`String tenantId`) -> `void`
- **getModule**(``) -> `String`
- **setModule**(`String module`) -> `void`
- **getAction**(``) -> `String`
- **setAction**(`String action`) -> `void`
- **getUserEmail**(``) -> `String`
- **setUserEmail**(`String userEmail`) -> `void`
- **getDetails**(``) -> `String`
- **setDetails**(`String details`) -> `void`
- **getTimestamp**(``) -> `LocalDateTime`
- **setTimestamp**(`LocalDateTime timestamp`) -> `void`

---

## Entity: BaseIdEntity
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\BaseIdEntity.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods
- **getId**(``) -> `Integer`
- **setId**(`Integer id`) -> `void`

---

## Entity: FeatureFlag
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\FeatureFlag.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `String` | `key` | @Column(name = "feature_key", nullable = false) |
| `boolean` | `isEnabled` | @Column(nullable = false) |
| `String` | `tenantId` | @Column(name = "tenant_id") |
| `String` | `description` | @Column(name = "tenant_id") |

### Methods
- **getId**(``) -> `Long`
- **setId**(`Long id`) -> `void`
- **getKey**(``) -> `String`
- **setKey**(`String key`) -> `void`
- **isEnabled**(``) -> `boolean`
- **setEnabled**(`boolean enabled`) -> `void`
- **getTenantId**(``) -> `String`
- **setTenantId**(`String tenantId`) -> `void`
- **getDescription**(``) -> `String`
- **setDescription**(`String description`) -> `void`

---

## Entity: AbstractAddressWithCountry
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\address\AbstractAddressWithCountry.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods
- **getCountry**(``) -> `Country`
- **setCountry**(`Country country`) -> `void`
- **toString**(``) -> `String`

---

## Entity: Address
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\address\Address.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Customer` | `customer` | @JoinColumn(name = "customer_id")<br>@ManyToOne |
| `boolean` | `defaultForShipping` | @Column(name = "default_address") |

### Methods
- **getCustomer**(``) -> `Customer`
- **setCustomer**(`Customer customer`) -> `void`
- **isDefaultForShipping**(``) -> `boolean`
- **setDefaultForShipping**(`boolean defaultForShipping`) -> `void`

---

## Entity: DailyProductStats
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\analytics\DailyProductStats.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `Date` | `date` | @Temporal(TemporalType.DATE)<br>@Column(nullable = false) |
| `Integer` | `productId` | @Column(name = "product_id", nullable = false) |
| `Integer` | `tenantId` | @Column(name = "tenant_id", nullable = false) |
| `Long` | `viewCount` | @Column(name = "view_count", nullable = false) |
| `Long` | `salesCount` | @Column(name = "sales_count", nullable = false) |
| `Double` | `revenue` | @Column(name = "revenue", nullable = false) |

### Methods
- **getId**(``) -> `Long`
- **setId**(`Long id`) -> `void`
- **getDate**(``) -> `Date`
- **setDate**(`Date date`) -> `void`
- **getProductId**(``) -> `Integer`
- **setProductId**(`Integer productId`) -> `void`
- **getTenantId**(``) -> `Integer`
- **setTenantId**(`Integer tenantId`) -> `void`
- **getViewCount**(``) -> `Long`
- **setViewCount**(`Long viewCount`) -> `void`
- **getSalesCount**(``) -> `Long`
- **setSalesCount**(`Long salesCount`) -> `void`
- **getRevenue**(``) -> `Double`
- **setRevenue**(`Double revenue`) -> `void`
- **incrementViews**(``) -> `void`
- **recordSale**(`Double amount`) -> `void`

---

## Entity: SearchKeyword
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\analytics\SearchKeyword.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `Integer` | `tenantId` | @Column(name = "tenant_id", nullable = false) |
| `String` | `keyword` | @Column(nullable = false, length = 100) |
| `Long` | `count` | @Column(nullable = false) |
| `Date` | `lastUpdated` | @Temporal(TemporalType.TIMESTAMP)<br>@Column(name = "last_updated", nullable = false) |

### Methods
- **getId**(``) -> `Long`
- **setId**(`Long id`) -> `void`
- **getTenantId**(``) -> `Integer`
- **setTenantId**(`Integer tenantId`) -> `void`
- **getKeyword**(``) -> `String`
- **setKeyword**(`String keyword`) -> `void`
- **getCount**(``) -> `Long`
- **setCount**(`Long count`) -> `void`
- **getLastUpdated**(``) -> `Date`
- **setLastUpdated**(`Date lastUpdated`) -> `void`
- **incrementCount**(``) -> `void`

---

## Entity: OrderCompletedEvent
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\analytics\event\OrderCompletedEvent.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `final` | `Integer` | - |
| `final` | `Integer` | - |
| `final` | `Double` | - |
| `final` | `java` | - |

### Methods
- **getOrderId**(``) -> `Integer`
- **getTenantId**(``) -> `Integer`
- **getTotalAmount**(``) -> `Double`

---

## Entity: ProductViewEvent
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\analytics\event\ProductViewEvent.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `final` | `Integer` | - |
| `final` | `Integer` | - |

### Methods
- **getProductId**(``) -> `Integer`
- **getTenantId**(``) -> `Integer`

---

## Entity: SearchEvent
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\analytics\event\SearchEvent.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `final` | `String` | - |
| `final` | `Integer` | - |

### Methods
- **getKeyword**(``) -> `String`
- **getTenantId**(``) -> `Integer`

---

## Entity: Article
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\articals\Article.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `title` | @Column(nullable = false, length = 256) |
| `String` | `content` | @Lob<br>@Column(nullable = false, columnDefinition = "LONGTEXT") |
| `String` | `alias` | @Column(nullable = false, length = 500) |
| `ArticleType` | `type` | @Enumerated(EnumType.ORDINAL) |
| `Date` | `updatedTime` | @Column(name = "updated_time") |
| `boolean` | `published` | - |
| `User` | `user` | @JoinColumn(name = "user_id")<br>@ManyToOne |

### Methods
- **getTitle**(``) -> `String`
- **setTitle**(`String title`) -> `void`
- **getContent**(``) -> `String`
- **setContent**(`String content`) -> `void`
- **getAlias**(``) -> `String`
- **setAlias**(`String alias`) -> `void`
- **getType**(``) -> `ArticleType`
- **setType**(`ArticleType type`) -> `void`
- **getUpdatedTime**(``) -> `Date`
- **setUpdatedTime**(`Date updatedTime`) -> `void`
- **isPublished**(``) -> `boolean`
- **setPublished**(`boolean published`) -> `void`
- **getUser**(``) -> `User`
- **setUser**(`User user`) -> `void`
- **toString**(``) -> `String`

---

## Entity: TenantAuditLog
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\audit\TenantAuditLog.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `User` | `user` | @JoinColumn(name = "user_id", nullable = false)<br>@ManyToOne(fetch = FetchType.LAZY) |
| `Tenant` | `tenant` | @JoinColumn(name = "tenant_id", nullable = false)<br>@ManyToOne(fetch = FetchType.LAZY) |
| `String` | `action` | @Column(nullable = false, length = 100) |
| `Tenant` | `targetTenant` | @JoinColumn(name = "target_tenant_id")<br>@ManyToOne(fetch = FetchType.LAZY) |
| `String` | `ipAddress` | @Column(name = "ip_address", length = 45) |
| `String` | `userAgent` | @Column(name = "user_agent", length = 500) |
| `Date` | `createdAt` | @Temporal(TemporalType.TIMESTAMP)<br>@Column(name = "created_at", nullable = false, updatable = false) |
| `String` | `details` | @Column(columnDefinition = "TEXT") |

### Methods
- **getId**(``) -> `Long`
- **setId**(`Long id`) -> `void`
- **getUser**(``) -> `User`
- **setUser**(`User user`) -> `void`
- **getTenant**(``) -> `Tenant`
- **setTenant**(`Tenant tenant`) -> `void`
- **getAction**(``) -> `String`
- **setAction**(`String action`) -> `void`
- **getTargetTenant**(``) -> `Tenant`
- **setTargetTenant**(`Tenant targetTenant`) -> `void`
- **getIpAddress**(``) -> `String`
- **setIpAddress**(`String ipAddress`) -> `void`
- **getUserAgent**(``) -> `String`
- **setUserAgent**(`String userAgent`) -> `void`
- **getCreatedAt**(``) -> `Date`
- **setCreatedAt**(`Date createdAt`) -> `void`
- **getDetails**(``) -> `String`
- **setDetails**(`String details`) -> `void`
- **toString**(``) -> `String`

---

## Entity: BillingTransaction
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\billing\BillingTransaction.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `double` | `amount` | @Column(nullable = false) |
| `BillingType` | `type` | @Column(nullable = false, length = 30)<br>@Enumerated(EnumType.STRING) |
| `Long` | `sourceTenantId` | @Column(name = "source_tenant_id", nullable = false) |
| `String` | `referenceId` | @Column(name = "reference_id", nullable = true) |
| `Date` | `transactionDate` | @Temporal(TemporalType.TIMESTAMP)<br>@Column(name = "transaction_date", nullable = false)<br>@Column(name = "reference_id", nullable = true) |

### Methods
- **getAmount**(``) -> `double`
- **setAmount**(`double amount`) -> `void`
- **getType**(``) -> `BillingType`
- **setType**(`BillingType type`) -> `void`
- **getSourceTenantId**(``) -> `Long`
- **setSourceTenantId**(`Long sourceTenantId`) -> `void`
- **getReferenceId**(``) -> `String`
- **setReferenceId**(`String referenceId`) -> `void`
- **getTransactionDate**(``) -> `Date`
- **setTransactionDate**(`Date transactionDate`) -> `void`

---

## Entity: Plan
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\billing\Plan.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | @Column(nullable = false, unique = true, length = 128) |
| `double` | `price` | @Column(nullable = false) |
| `String` | `billingCycle` | @Column(nullable = false, length = 20) |
| `String` | `description` | @Column(length = 2048)<br>@Column(nullable = false, length = 20) |
| `double` | `commissionRate` | @Column(name = "commission_rate", nullable = false) |
| `Set<String>` | `features` | @Column(name = "feature")<br>@CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))<br>@ElementCollection<br>@Column(name = "commission_rate", nullable = false) |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getPrice**(``) -> `double`
- **setPrice**(`double price`) -> `void`
- **getBillingCycle**(``) -> `String`
- **setBillingCycle**(`String billingCycle`) -> `void`
- **getDescription**(``) -> `String`
- **setDescription**(`String description`) -> `void`
- **getCommissionRate**(``) -> `double`
- **setCommissionRate**(`double commissionRate`) -> `void`
- **getFeatures**(``) -> `Set<String>`
- **setFeatures**(`Set<String> features`) -> `void`

---

## Entity: Subscription
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\billing\Subscription.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Plan` | `plan` | @JoinColumn(name = "plan_id", nullable = false)<br>@ManyToOne(fetch = FetchType.LAZY) |
| `Long` | `targetTenantId` | @Column(name = "target_tenant_id", nullable = false) |
| `SubscriptionStatus` | `status` | @Column(nullable = false, length = 20)<br>@Enumerated(EnumType.STRING) |
| `Date` | `startDate` | @Temporal(TemporalType.DATE)<br>@Column(name = "start_date", nullable = false) |
| `Date` | `nextBillingDate` | @Temporal(TemporalType.DATE)<br>@Column(name = "next_billing_date") |

### Methods
- **getPlan**(``) -> `Plan`
- **setPlan**(`Plan plan`) -> `void`
- **getTargetTenantId**(``) -> `Long`
- **setTargetTenantId**(`Long targetTenantId`) -> `void`
- **getStatus**(``) -> `SubscriptionStatus`
- **setStatus**(`SubscriptionStatus status`) -> `void`
- **getStartDate**(``) -> `Date`
- **setStartDate**(`Date startDate`) -> `void`
- **getNextBillingDate**(``) -> `Date`
- **setNextBillingDate**(`Date nextBillingDate`) -> `void`

---

## Entity: Booking
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\booking\Booking.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | @Column(nullable = false, length = 150) |
| `BookingType` | `bookingType` | @Column(name = "booking_type", nullable = false, length = 20)<br>@Enumerated(EnumType.STRING)<br>@Column(nullable = false, length = 150) |
| `String` | `customerName` | @Column(name = "customer_name", nullable = false, length = 150) |
| `Date` | `startTime` | @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")<br>@Temporal(TemporalType.TIMESTAMP)<br>@Column(name = "start_time") |
| `Date` | `endTime` | @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")<br>@Temporal(TemporalType.TIMESTAMP)<br>@Column(name = "end_time") |
| `String` | `status` | @Column(length = 20) |
| `String` | `details` | @Column(length = 500)<br>@Column(length = 20) |
| `Long` | `tenantId` | @Column(name = "tenant_id")<br>@Column(length = 500)<br>@Column(length = 20) |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getBookingType**(``) -> `BookingType`
- **setBookingType**(`BookingType bookingType`) -> `void`
- **getCustomerName**(``) -> `String`
- **setCustomerName**(`String customerName`) -> `void`
- **getStartTime**(``) -> `Date`
- **setStartTime**(`Date startTime`) -> `void`
- **getEndTime**(``) -> `Date`
- **setEndTime**(`Date endTime`) -> `void`
- **getStatus**(``) -> `String`
- **setStatus**(`String status`) -> `void`
- **getDetails**(``) -> `String`
- **setDetails**(`String details`) -> `void`
- **getTenantId**(``) -> `Long`
- **setTenantId**(`Long tenantId`) -> `void`

---

## Entity: Brand
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\brand\Brand.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | @Column(nullable = false, length = 45) |
| `String` | `logo` | @Column(nullable = false, length = 128) |
| `Set<Category>` | `categories` | @JoinTable(name = "brands_categories", joinColumns = @JoinColumn(name = "brand_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))<br>@ManyToMany(fetch = FetchType.LAZY) |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getLogo**(``) -> `String`
- **setLogo**(`String logo`) -> `void`
- **getCategories**(``) -> `Set<Category>`
- **setCategories**(`Set<Category> categories`) -> `void`
- **getImagePath**(``) -> `String`
- **getImageDir**(``) -> `String`
- **toString**(``) -> `String`

---

## Entity: Category
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\category\Category.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `tenantId` | @Column(name = "tenant_id", updatable = false) |
| `String` | `name` | @Column(name = "name", length = 85, nullable = false) |
| `String` | `alias` | @Column(name = "alias", length = 85, nullable = false) |
| `String` | `image` | @Column(name = "image", length = 125, nullable = true) |
| `boolean` | `enabled` | @Column(name = "enabled") |
| `int` | `level` | - |

### Methods
- **getTenantId**(``) -> `Long`
- **setTenantId**(`Long tenantId`) -> `void`
- **setLevel**(`int level`) -> `void`
- **getLevel**(``) -> `int`
- **copyIdAndName**(`Integer id, String name`) -> `Category`
- **copyFull**(`Category category`) -> `Category`
- **copyFull**(`Category Category, String name`) -> `Category`
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getAlias**(``) -> `String`
- **setAlias**(`String alias`) -> `void`
- **getImage**(``) -> `String`
- **setImage**(`String image`) -> `void`
- **isEnabled**(``) -> `boolean`
- **setEnabled**(`boolean enabled`) -> `void`
- **getCatImagePath**(``) -> `String`
- **getImageDir**(``) -> `String`
- **compareTo**(`Category other`) -> `int`

---

## Entity: ConvertedFile
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\convertPdf\ConvertedFile.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `int` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `String` | `fileName` | - |
| `FileExtension` | `extension` | - |
| `FileConvert` | `fileConvert` | @JoinColumn(name = "product_id")<br>@ManyToOne |

### Methods
- **getId**(``) -> `int`
- **setId**(`int id`) -> `void`
- **getFileName**(``) -> `String`
- **setFileName**(`String fileName`) -> `void`
- **getExtension**(``) -> `FileExtension`
- **setExtension**(`FileExtension extension`) -> `void`
- **getFileConvert**(``) -> `FileConvert`
- **setFileConvert**(`FileConvert fileConvert`) -> `void`
- **convertedFilePath**(``) -> `String`
- **convertedFileDir**(``) -> `String`

---

## Entity: FileConvert
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\convertPdf\FileConvert.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Integer` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `String` | `filename` | @Column(nullable = false) |
| `FileConvert` | `fileConvert` | @JoinColumn(name = "parent_id")  // Correct use of @JoinColumn<br>@ManyToOne(fetch = FetchType.LAZY) |
| `Set<FileConvert>` | `convertedFiles` | @OneToMany(mappedBy = "fileConvert", fetch = FetchType.LAZY, cascade = CascadeType.ALL) |
| `FileExtension` | `extension` | @Enumerated(EnumType.STRING) |

### Methods
- **getExtension**(``) -> `FileExtension`
- **setExtension**(`FileExtension extension`) -> `void`
- **getFilePath**(``) -> `String`
- **getFileDir**(``) -> `String`
- **getId**(``) -> `Integer`
- **setId**(`Integer id`) -> `void`
- **getFilename**(``) -> `String`
- **setFilename**(`String filename`) -> `void`
- **getFileConvert**(``) -> `FileConvert`
- **setFileConvert**(`FileConvert fileConvert`) -> `void`
- **getConvertedFiles**(``) -> `Set<FileConvert>`
- **setConvertedFiles**(`Set<FileConvert> convertedFiles`) -> `void`

---

## Entity: Customer
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\customer\Customer.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `email` | @Column(nullable = false, length = 45) |
| `String` | `password` | @Column(nullable = false, length = 64) |
| `String` | `verificationCode` | @Column(name = "verification_code", length = 64) |
| `boolean` | `enabled` | - |
| `Integer` | `pointsBalance` | @Column(name = "points_balance") |
| `Date` | `createdTime` | @Column(name = "created_time") |
| `String` | `image` | @Column(name = "image", length = 225, nullable = true) |
| `AuthenticationType` | `authenticationType` | @Column(name = "authentication_type", length = 10)<br>@Enumerated(EnumType.STRING) |
| `String` | `restPasswordToken` | @Column(name = "rest_password_token", length = 30) |
| `Date` | `lastLoginTime` | @Column(name = "last_login_time") |
| `List<CartItem>` | `cartItems` | @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true) |

### Methods
- **getCartItems**(``) -> `List<CartItem>`
- **setCartItems**(`List<CartItem> cartItems`) -> `void`
- **getEmail**(``) -> `String`
- **setEmail**(`String email`) -> `void`
- **getImage**(``) -> `String`
- **setImage**(`String image`) -> `void`
- **isEnabled**(``) -> `boolean`
- **setEnabled**(`boolean enabled`) -> `void`
- **getPointsBalance**(``) -> `Integer`
- **setPointsBalance**(`Integer pointsBalance`) -> `void`
- **getPassword**(``) -> `String`
- **setPassword**(`String password`) -> `void`
- **getVerificationCode**(``) -> `String`
- **setVerificationCode**(`String verificationCode`) -> `void`
- **getCreatedTime**(``) -> `Date`
- **setCreatedTime**(`Date createdTime`) -> `void`
- **getAuthenticationType**(``) -> `AuthenticationType`
- **setAuthenticationType**(`AuthenticationType authenticationType`) -> `void`
- **getRestPasswordToken**(``) -> `String`
- **setRestPasswordToken**(`String restPasswordToken`) -> `void`
- **getLastLoginTime**(``) -> `Date`
- **setLastLoginTime**(`Date lastLoginTime`) -> `void`
- **getFullName**(``) -> `String`
- **setFullName**(`String name`) -> `void`
- **getImagePath**(``) -> `String`
- **getImageDir**(``) -> `String`

---

## Entity: CustomerPoint
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\customer\CustomerPoint.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `tenantId` | @Column(name = "tenant_id", updatable = false) |
| `Customer` | `customer` | @JoinColumn(name = "customer_id", nullable = false)<br>@ManyToOne(fetch = FetchType.LAZY) |
| `int` | `points` | @Column(nullable = false) |
| `PointType` | `type` | @Column(length = 20, nullable = false)<br>@Enumerated(EnumType.STRING)<br>@Column(nullable = false) |
| `String` | `description` | @Column(length = 255) |
| `Date` | `transactionDate` | @Column(name = "transaction_date")<br>@Column(length = 255) |

### Methods
- **getTenantId**(``) -> `Long`
- **setTenantId**(`Long tenantId`) -> `void`
- **getCustomer**(``) -> `Customer`
- **setCustomer**(`Customer customer`) -> `void`
- **getPoints**(``) -> `int`
- **setPoints**(`int points`) -> `void`
- **getType**(``) -> `PointType`
- **setType**(`PointType type`) -> `void`
- **getDescription**(``) -> `String`
- **setDescription**(`String description`) -> `void`
- **getTransactionDate**(``) -> `Date`
- **setTransactionDate**(`Date transactionDate`) -> `void`

---

## Entity: ArticleNotFoundException
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\exception\ArticleNotFoundException.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods

---

## Entity: BookingNotFoundException
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\exception\BookingNotFoundException.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods

---

## Entity: CategoryNotFoundException
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\exception\CategoryNotFoundException.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods

---

## Entity: CustomerNotFoundException
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\exception\CustomerNotFoundException.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods

---

## Entity: OrderNotFoundException
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\exception\OrderNotFoundException.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods

---

## Entity: ProductNotFoundException
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\exception\ProductNotFoundException.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods

---

## Entity: ReviewNotFoundException
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\exception\ReviewNotFoundException.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods

---

## Entity: ShippingRateNotFoundException
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\exception\ShippingRateNotFoundException.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods

---

## Entity: Menu
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\menu\Menu.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `MenuType` | `type` | @Enumerated(EnumType.ORDINAL) |
| `String` | `title` | @Column(nullable = false, length = 128) |
| `String` | `alias` | @Column(nullable = false, length = 256) |
| `int` | `position` | - |
| `boolean` | `enabled` | - |
| `Article` | `article` | @JoinColumn(name = "article_id")<br>@ManyToOne |

### Methods
- **getType**(``) -> `MenuType`
- **setType**(`MenuType type`) -> `void`
- **getTitle**(``) -> `String`
- **setTitle**(`String title`) -> `void`
- **getAlias**(``) -> `String`
- **setAlias**(`String alias`) -> `void`
- **getPosition**(``) -> `int`
- **setPosition**(`int position`) -> `void`
- **isEnabled**(``) -> `boolean`
- **setEnabled**(`boolean enabled`) -> `void`
- **getArticle**(``) -> `Article`
- **setArticle**(`Article article`) -> `void`
- **toString**(``) -> `String`

---

## Entity: Order
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\order\Order.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `country` | @Column(nullable = false, length = 45) |
| `Date` | `orderTime` | - |
| `float` | `shippingCost` | - |
| `float` | `productCost` | - |
| `float` | `subtotal` | - |
| `float` | `tax` | - |
| `float` | `total` | - |
| `int` | `deliverDays` | - |
| `Date` | `deliverDate` | - |
| `PaymentMethod` | `paymentMethod` | @Enumerated(EnumType.STRING) |
| `OrderStatus` | `status` | @Enumerated(EnumType.STRING) |
| `Customer` | `customer` | @JoinColumn(name = "customer_id")<br>@ManyToOne |
| `Set<OrderDetail>` | `orderDetails` | @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) |
| `List<OrderTrack>` | `orderTracks` | @OrderBy("updatedTime ASC")<br>@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) |

### Methods
- **getCountry**(``) -> `String`
- **setCountry**(`String country`) -> `void`
- **getOrderTime**(``) -> `Date`
- **setOrderTime**(`Date orderTime`) -> `void`
- **getShippingCost**(``) -> `float`
- **setShippingCost**(`float shippingCost`) -> `void`
- **getProductCost**(``) -> `float`
- **setProductCost**(`float productCost`) -> `void`
- **getSubtotal**(``) -> `float`
- **setSubtotal**(`float subtotal`) -> `void`
- **getTax**(``) -> `float`
- **setTax**(`float tax`) -> `void`
- **getTotal**(``) -> `float`
- **setTotal**(`float total`) -> `void`
- **getDeliverDays**(``) -> `int`
- **setDeliverDays**(`int deliverDays`) -> `void`
- **getDeliverDate**(``) -> `Date`
- **setDeliverDate**(`Date deliverDate`) -> `void`
- **getPaymentMethod**(``) -> `PaymentMethod`
- **setPaymentMethod**(`PaymentMethod paymentMethod`) -> `void`
- **getStatus**(``) -> `OrderStatus`
- **setStatus**(`OrderStatus status`) -> `void`
- **getCustomer**(``) -> `Customer`
- **setCustomer**(`Customer customer`) -> `void`
- **getOrderDetails**(``) -> `Set<OrderDetail>`
- **setOrderDetails**(`Set<OrderDetail> orderDetails`) -> `void`
- **getOrderTracks**(``) -> `List<OrderTrack>`
- **setOrderTracks**(`List<OrderTrack> orderTracks`) -> `void`
- **copyAddressFromCustomer**(``) -> `void`
- **copyShippingAddress**(`Address address`) -> `void`
- **toString**(``) -> `String`
- **getDestination**(``) -> `String`
- **getShippingAddress**(``) -> `String`
- **getDeliverDateOnForm**(``) -> `String`
- **setDeliverDateOnForm**(`String dateString`) -> `void`
- **getRecipientName**(``) -> `String`
- **getRecipientAddress**(``) -> `String`
- **isCOD**(``) -> `boolean`
- **isNew**(``) -> `boolean`
- **getStatusBasedOnTrack**(``) -> `OrderStatus`
- **isProcessing**(``) -> `boolean`
- **isPackaged**(``) -> `boolean`
- **isPicked**(``) -> `boolean`
- **isShipping**(``) -> `boolean`
- **isDelivered**(``) -> `boolean`
- **isReturnRequested**(``) -> `boolean`
- **isReturned**(``) -> `boolean`
- **hasStatus**(`OrderStatus status`) -> `boolean`
- **getProductNames**(``) -> `String`

---

## Entity: OrderDetail
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\order\OrderDetail.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `int` | `quantity` | - |
| `float` | `productCost` | - |
| `float` | `shippingCost` | - |
| `float` | `unitPrice` | - |
| `float` | `subtotal` | - |
| `Product` | `product` | @JoinColumn(name = "product_id")<br>@ManyToOne |
| `Order` | `order` | @JoinColumn(name = "order_id")<br>@ManyToOne |

### Methods
- **getQuantity**(``) -> `int`
- **setQuantity**(`int quantity`) -> `void`
- **getProductCost**(``) -> `float`
- **setProductCost**(`float productCost`) -> `void`
- **getShippingCost**(``) -> `float`
- **setShippingCost**(`float shippingCost`) -> `void`
- **getUnitPrice**(``) -> `float`
- **setUnitPrice**(`float unitPrice`) -> `void`
- **getSubtotal**(``) -> `float`
- **setSubtotal**(`float subtotal`) -> `void`
- **getProduct**(``) -> `Product`
- **setProduct**(`Product product`) -> `void`
- **getOrder**(``) -> `Order`
- **setOrder**(`Order order`) -> `void`
- **toString**(``) -> `String`

---

## Entity: OrderTrack
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\order\OrderTrack.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `notes` | @Column(length = 256) |
| `Date` | `updatedTime` | - |
| `OrderStatus` | `status` | @Column(length = 45, nullable = false)<br>@Enumerated(EnumType.STRING) |
| `Order` | `order` | @JoinColumn(name = "order_id")<br>@ManyToOne |

### Methods
- **getNotes**(``) -> `String`
- **setNotes**(`String notes`) -> `void`
- **getUpdatedTime**(``) -> `Date`
- **setUpdatedTime**(`Date updatedTime`) -> `void`
- **getStatus**(``) -> `OrderStatus`
- **setStatus**(`OrderStatus status`) -> `void`
- **getOrder**(``) -> `Order`
- **setOrder**(`Order order`) -> `void`
- **getUpdatedTimeOnForm**(``) -> `String`
- **setUpdatedTimeOnForm**(`String dateString`) -> `void`
- **toString**(``) -> `String`

---

## Entity: Product
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\product\Product.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `ProductType` | `productType` | @Column(name = "product_type", nullable = false, length = 50)<br>@Enumerated(EnumType.STRING) |
| `String` | `serviceDuration` | @Column(name = "service_duration") |
| `String` | `subscriptionInterval` | @Column(name = "subscription_interval")<br>@Column(name = "service_duration") |
| `Integer` | `bookingSlots` | @Column(name = "booking_slots")<br>@Column(name = "subscription_interval")<br>@Column(name = "service_duration") |
| `Boolean` | `hasDescription` | @Column(name = "has_description")<br>@Column(name = "booking_slots")<br>@Column(name = "subscription_interval")<br>@Column(name = "service_duration") |
| `Boolean` | `hasExtraDetails` | @Column(name = "has_extra_details")<br>@Column(name = "has_description")<br>@Column(name = "booking_slots")<br>@Column(name = "subscription_interval")<br>@Column(name = "service_duration") |
| `Boolean` | `trackStock` | @Column(name = "track_stock") |
| `Boolean` | `hasShipping` | @Column(name = "has_shipping") |
| `Boolean` | `hasScheduling` | @Column(name = "has_scheduling") |
| `Boolean` | `hasLocation` | @Column(name = "has_location") |
| `boolean` | `inStock` | @Column(name = "in_stock") |
| `float` | `length` | - |
| `float` | `width` | - |
| `float` | `height` | - |
| `float` | `weight` | - |
| `Set<ProductImage>` | `images` | @OneToMany(mappedBy = "product", cascade = CascadeType.ALL) |
| `final` | `List` | @OneToMany(mappedBy = "product", cascade = CascadeType.ALL) |
| `int` | `reviewCount` | - |
| `float` | `averageRating` | - |
| `Category` | `category` | @JoinColumn(name = "category_id")<br>@ManyToOne |
| `Brand` | `brand` | @JoinColumn(name = "brand_id")<br>@ManyToOne |
| `boolean` | `customerCanReview` | @Transient |
| `boolean` | `reviewedByCustomer` | @Transient |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getAlias**(``) -> `String`
- **setAlias**(`String alias`) -> `void`
- **getShortDescription**(``) -> `String`
- **setShortDescription**(`String shortDescription`) -> `void`
- **getFullDescription**(``) -> `String`
- **setFullDescription**(`String fullDescription`) -> `void`
- **getCreatedTime**(``) -> `Date`
- **setCreatedTime**(`Date createdTime`) -> `void`
- **getUpdatedTime**(``) -> `Date`
- **setUpdatedTime**(`Date updatedTime`) -> `void`
- **isEnabled**(``) -> `boolean`
- **setEnabled**(`boolean enabled`) -> `void`
- **getMainImage**(``) -> `String`
- **setMainImage**(`String mainImage`) -> `void`
- **getCost**(``) -> `float`
- **setCost**(`float cost`) -> `void`
- **getPrice**(``) -> `float`
- **setPrice**(`float price`) -> `void`
- **getDiscountPercent**(``) -> `float`
- **setDiscountPercent**(`float discountPercent`) -> `void`
- **getProductType**(``) -> `ProductType`
- **setProductType**(`ProductType productType`) -> `void`
- **getServiceDuration**(``) -> `String`
- **setServiceDuration**(`String serviceDuration`) -> `void`
- **getSubscriptionInterval**(``) -> `String`
- **setSubscriptionInterval**(`String subscriptionInterval`) -> `void`
- **getBookingSlots**(``) -> `Integer`
- **setBookingSlots**(`Integer bookingSlots`) -> `void`
- **isInStock**(``) -> `boolean`
- **setInStock**(`boolean inStock`) -> `void`
- **getLength**(``) -> `float`
- **setLength**(`float length`) -> `void`
- **getWidth**(``) -> `float`
- **setWidth**(`float width`) -> `void`
- **getHeight**(``) -> `float`
- **setHeight**(`float height`) -> `void`
- **getWeight**(``) -> `float`
- **setWeight**(`float weight`) -> `void`
- **getHasDescription**(``) -> `Boolean`
- **setHasDescription**(`Boolean hasDescription`) -> `void`
- **getHasExtraDetails**(``) -> `Boolean`
- **setHasExtraDetails**(`Boolean hasExtraDetails`) -> `void`
- **getTrackStock**(``) -> `Boolean`
- **setTrackStock**(`Boolean trackStock`) -> `void`
- **getHasShipping**(``) -> `Boolean`
- **setHasShipping**(`Boolean hasShipping`) -> `void`
- **getHasScheduling**(``) -> `Boolean`
- **setHasScheduling**(`Boolean hasScheduling`) -> `void`
- **getHasLocation**(``) -> `Boolean`
- **setHasLocation**(`Boolean hasLocation`) -> `void`
- **getDiscountPrice**(``) -> `float`
- **getShortName**(``) -> `String`
- **getReviewCount**(``) -> `int`
- **setReviewCount**(`int reviewCount`) -> `void`
- **getAverageRating**(``) -> `float`
- **setAverageRating**(`float averageRating`) -> `void`
- **isCustomerCanReview**(``) -> `boolean`
- **setCustomerCanReview**(`boolean customerCanReview`) -> `void`
- **isReviewedByCustomer**(``) -> `boolean`
- **setReviewedByCustomer**(`boolean reviewedByCustomer`) -> `void`
- **getDetails**(``) -> `List<ProductDetails>`
- **addProductDetails**(`Integer id, String detailName, String detailValue, Long tenantId`) -> `void`
- **addProductDetails**(`String detailName, String detailValue, Long tenantId`) -> `void`
- **addProductDetailsTenantId**(`Long tenantId`) -> `void`
- **addExtraImages**(`String imageName`) -> `void`
- **getImages**(``) -> `Set<ProductImage>`
- **setImages**(`Set<ProductImage> images`) -> `void`
- **getCategory**(``) -> `Category`
- **setCategory**(`Category category`) -> `void`
- **getBrand**(``) -> `Brand`
- **setBrand**(`Brand brand`) -> `void`
- **getImagePath**(``) -> `String`
- **getExtraImagesPath**(``) -> `String`
- **getImageDir**(``) -> `String`
- **getExtraImageDir**(``) -> `String`
- **getURI**(``) -> `String`

---

## Entity: ProductDetails
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\product\ProductDetails.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | @Column(nullable = false, length = 255) |
| `String` | `value` | @Column(nullable = false, length = 255) |
| `Product` | `product` | @JoinColumn(name = "product_id")<br>@ManyToOne |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getValue**(``) -> `String`
- **setValue**(`String value`) -> `void`
- **getProduct**(``) -> `Product`
- **setProduct**(`Product product`) -> `void`

---

## Entity: ProductImage
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\product\ProductImage.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | @Column(nullable = false) |
| `Product` | `product` | @JoinColumn(name = "product_id")<br>@ManyToOne |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getProduct**(``) -> `Product`
- **setProduct**(`Product product`) -> `void`
- **getImagePath**(``) -> `String`

---

## Entity: Question
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\question\Question.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `questionContent` | @Column(name = "question_content", nullable = false, length = 500) |
| `String` | `answer` | @Column(name = "answer", length = 1000) |
| `int` | `votes` | @Column(name = "votes") |
| `boolean` | `approved` | @Column(name = "approved") |
| `Date` | `askTime` | @Column(name = "ask_time") |
| `Date` | `answerTime` | @Column(name = "answer_time") |
| `Product` | `product` | @JoinColumn(name = "product_id")<br>@ManyToOne(fetch = FetchType.LAZY) |
| `Customer` | `asker` | @JoinColumn(name = "asker_id")<br>@ManyToOne(fetch = FetchType.LAZY) |
| `User` | `answerer` | @JoinColumn(name = "answerer_id")<br>@ManyToOne(fetch = FetchType.LAZY) |
| `boolean` | `upvotedByCurrentCustomer` | @Transient |
| `boolean` | `downvotedByCurrentCustomer` | @Transient |

### Methods
- **getQuestionContent**(``) -> `String`
- **setQuestionContent**(`String questionContent`) -> `void`
- **getAnswer**(``) -> `String`
- **setAnswer**(`String answer`) -> `void`
- **getVotes**(``) -> `int`
- **setVotes**(`int votes`) -> `void`
- **isApproved**(``) -> `boolean`
- **setApproved**(`boolean approved`) -> `void`
- **getAskTime**(``) -> `Date`
- **setAskTime**(`Date askTime`) -> `void`
- **getAnswerTime**(``) -> `Date`
- **setAnswerTime**(`Date answerTime`) -> `void`
- **getProduct**(``) -> `Product`
- **setProduct**(`Product product`) -> `void`
- **getAsker**(``) -> `Customer`
- **setAsker**(`Customer asker`) -> `void`
- **getAnswerer**(``) -> `User`
- **setAnswerer**(`User answerer`) -> `void`
- **isAnswered**(``) -> `boolean`
- **isUpvotedByCurrentCustomer**(``) -> `boolean`
- **setUpvotedByCurrentCustomer**(`boolean upvotedByCurrentCustomer`) -> `void`
- **isDownvotedByCurrentCustomer**(``) -> `boolean`
- **setDownvotedByCurrentCustomer**(`boolean downvotedByCurrentCustomer`) -> `void`

---

## Entity: QuestionVote
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\question\QuestionVote.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `static` | `final` | - |
| `static` | `final` | - |
| `int` | `votes` | - |
| `Customer` | `customer` | @JoinColumn(name = "customer_id")<br>@ManyToOne |
| `Question` | `question` | @JoinColumn(name = "question_id")<br>@ManyToOne |

### Methods
- **getVotes**(``) -> `int`
- **setVotes**(`int votes`) -> `void`
- **getCustomer**(``) -> `Customer`
- **setCustomer**(`Customer customer`) -> `void`
- **getQuestion**(``) -> `Question`
- **setQuestion**(`Question question`) -> `void`
- **voteUp**(``) -> `void`
- **voteDown**(``) -> `void`
- **isUpvoted**(``) -> `boolean`
- **isDownvoted**(``) -> `boolean`
- **toString**(``) -> `String`

---

## Entity: Review
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\Review\Review.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `headline` | @Column(length = 128, nullable = false) |
| `String` | `comment` | @Column(length = 300, nullable = false) |
| `float` | `rating` | - |
| `int` | `votes` | - |
| `Date` | `reviewTime` | @Column(nullable = false) |
| `Product` | `product` | @JoinColumn(name = "product_id")<br>@ManyToOne |
| `Customer` | `customer` | @JoinColumn(name = "customer_id")<br>@ManyToOne |
| `boolean` | `upvotedByCurrentCustomer` | @Transient |
| `boolean` | `downvotedByCurrentCustomer` | @Transient |

### Methods
- **getHeadline**(``) -> `String`
- **setHeadline**(`String headline`) -> `void`
- **getComment**(``) -> `String`
- **setComment**(`String comment`) -> `void`
- **getRating**(``) -> `float`
- **setRating**(`float rating`) -> `void`
- **getVotes**(``) -> `int`
- **setVotes**(`int votes`) -> `void`
- **getReviewTime**(``) -> `Date`
- **setReviewTime**(`Date reviewTime`) -> `void`
- **getProduct**(``) -> `Product`
- **setProduct**(`Product product`) -> `void`
- **getCustomer**(``) -> `Customer`
- **setCustomer**(`Customer customer`) -> `void`
- **isUpvotedByCurrentCustomer**(``) -> `boolean`
- **setUpvotedByCurrentCustomer**(`boolean upvotedByCurrentCustomer`) -> `void`
- **isDownvotedByCurrentCustomer**(``) -> `boolean`
- **setDownvotedByCurrentCustomer**(`boolean downvotedByCurrentCustomer`) -> `void`
- **toString**(``) -> `String`
- **hashCode**(``) -> `int`
- **equals**(`Object obj`) -> `boolean`

---

## Entity: ReviewVote
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\Review\ReviewVote.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `static` | `final` | - |
| `static` | `final` | - |
| `int` | `votes` | - |
| `Customer` | `customer` | @JoinColumn(name = "customer_id")<br>@ManyToOne |
| `Review` | `review` | @JoinColumn(name = "review_id")<br>@ManyToOne |

### Methods
- **getVotes**(``) -> `int`
- **setVotes**(`int votes`) -> `void`
- **getCustomer**(``) -> `Customer`
- **setCustomer**(`Customer customer`) -> `void`
- **getReview**(``) -> `Review`
- **setReview**(`Review review`) -> `void`
- **voteUp**(``) -> `void`
- **voteDown**(``) -> `void`
- **toString**(``) -> `String`
- **isUpvoted**(``) -> `boolean`
- **isDownvoted**(``) -> `boolean`

---

## Entity: ArticleSection
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\section\ArticleSection.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `int` | `articleOrder` | @Column(name = "article_order") |
| `Article` | `article` | @JoinColumn(name = "article_id")<br>@ManyToOne |

### Methods
- **getArticleOrder**(``) -> `int`
- **setArticleOrder**(`int articleOrder`) -> `void`
- **getArticle**(``) -> `Article`
- **setArticle**(`Article article`) -> `void`

---

## Entity: BrandSection
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\section\BrandSection.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `int` | `brandOrder` | @Column(name = "brand_order") |
| `Brand` | `brand` | @JoinColumn(name = "brand_id")<br>@ManyToOne |

### Methods
- **getBrandOrder**(``) -> `int`
- **setBrandOrder**(`int brandOrder`) -> `void`
- **getBrand**(``) -> `Brand`
- **setBrand**(`Brand brand`) -> `void`

---

## Entity: CategorySection
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\section\CategorySection.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `int` | `categoryOrder` | @Column(name = "category_order") |
| `Category` | `category` | @JoinColumn(name = "category_id")<br>@ManyToOne |

### Methods
- **getCategoryOrder**(``) -> `int`
- **setCategoryOrder**(`int categoryOrder`) -> `void`
- **getCategory**(``) -> `Category`
- **setCategory**(`Category category`) -> `void`

---

## Entity: ProductSection
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\section\ProductSection.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `int` | `productOrder` | @Column(name = "product_order") |
| `Product` | `product` | @JoinColumn(name = "product_id")<br>@ManyToOne |

### Methods
- **getProductOrder**(``) -> `int`
- **setProductOrder**(`int productOrder`) -> `void`
- **getProduct**(``) -> `Product`
- **setProduct**(`Product product`) -> `void`

---

## Entity: Section
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\section\Section.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `heading` | @Column(length = 256, nullable = false) |
| `String` | `description` | @Column(length = 2048, nullable = false) |
| `boolean` | `enabled` | - |
| `int` | `sectionOrder` | @Column(name = "section_order") |
| `SectionType` | `type` | @Enumerated(EnumType.STRING) |
| `List<ProductSection>` | `productSections` | @OrderBy("productOrder ASC")<br>@JoinColumn(name = "section_id")<br>@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) |
| `List<CategorySection>` | `categorySections` | @OrderBy("categoryOrder ASC")<br>@JoinColumn(name = "section_id")<br>@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) |
| `List<BrandSection>` | `brandSections` | @OrderBy("brandOrder ASC")<br>@JoinColumn(name = "section_id")<br>@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) |
| `List<ArticleSection>` | `articleSections` | @OrderBy("articleOrder ASC")<br>@JoinColumn(name = "section_id")<br>@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) |

### Methods
- **getHeading**(``) -> `String`
- **setHeading**(`String heading`) -> `void`
- **getDescription**(``) -> `String`
- **setDescription**(`String description`) -> `void`
- **isEnabled**(``) -> `boolean`
- **setEnabled**(`boolean enabled`) -> `void`
- **getSectionOrder**(``) -> `int`
- **setSectionOrder**(`int sectionOrder`) -> `void`
- **getType**(``) -> `SectionType`
- **setType**(`SectionType type`) -> `void`
- **getProductSections**(``) -> `List<ProductSection>`
- **setProductSections**(`List<ProductSection> productSections`) -> `void`
- **addProductSection**(`ProductSection productSection`) -> `void`
- **hashCode**(``) -> `int`
- **equals**(`Object obj`) -> `boolean`
- **getCategorySections**(``) -> `List<CategorySection>`
- **setCategorySections**(`List<CategorySection> categorySections`) -> `void`
- **addCategorySection**(`CategorySection categorySection`) -> `void`
- **getBrandSections**(``) -> `List<BrandSection>`
- **setBrandSections**(`List<BrandSection> brandSections`) -> `void`
- **addBrandSection**(`BrandSection brandSection`) -> `void`
- **getArticleSections**(``) -> `List<ArticleSection>`
- **setArticleSections**(`List<ArticleSection> articleSections`) -> `void`
- **addArticleSection**(`ArticleSection articleSection`) -> `void`
- **toString**(``) -> `String`

---

## Entity: Service
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\service\Service.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `int` | `duration` | - |
| `int` | `bufferTime` | - |
| `ServiceLocationType` | `locationType` | @Column(name = "location_type")<br>@Enumerated(EnumType.STRING) |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getAlias**(``) -> `String`
- **setAlias**(`String alias`) -> `void`
- **getShortDescription**(``) -> `String`
- **setShortDescription**(`String shortDescription`) -> `void`
- **getFullDescription**(``) -> `String`
- **setFullDescription**(`String fullDescription`) -> `void`
- **getCreatedTime**(``) -> `Date`
- **setCreatedTime**(`Date createdTime`) -> `void`
- **getUpdatedTime**(``) -> `Date`
- **setUpdatedTime**(`Date updatedTime`) -> `void`
- **isEnabled**(``) -> `boolean`
- **setEnabled**(`boolean enabled`) -> `void`
- **getMainImage**(``) -> `String`
- **setMainImage**(`String mainImage`) -> `void`
- **getCost**(``) -> `float`
- **setCost**(`float cost`) -> `void`
- **getPrice**(``) -> `float`
- **setPrice**(`float price`) -> `void`
- **getDiscountPercent**(``) -> `float`
- **setDiscountPercent**(`float discountPercent`) -> `void`
- **getDiscountPrice**(``) -> `float`
- **getShortName**(``) -> `String`
- **getDuration**(``) -> `int`
- **setDuration**(`int duration`) -> `void`
- **getBufferTime**(``) -> `int`
- **setBufferTime**(`int bufferTime`) -> `void`
- **getLocationType**(``) -> `ServiceLocationType`
- **setLocationType**(`ServiceLocationType locationType`) -> `void`
- **getImagePath**(``) -> `String`

---

## Entity: DefaultTheme
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\DefaultTheme.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods

---

## Entity: RewardSetting
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\RewardSetting.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `tenantId` | @Column(name = "tenant_id", updatable = false) |
| `float` | `earningRate` | @Column(name = "earning_rate", nullable = false) |
| `float` | `redeemRate` | @Column(name = "redeem_rate", nullable = false)<br>@Column(name = "earning_rate", nullable = false) |
| `int` | `signUpPoints` | @Column(name = "sign_up_points")<br>@Column(name = "redeem_rate", nullable = false)<br>@Column(name = "earning_rate", nullable = false) |
| `int` | `pointExpiryDays` | @Column(name = "point_expiry_days") |
| `int` | `reviewPoints` | @Column(name = "review_points") |
| `int` | `minPointsToRedeem` | @Column(name = "min_points_to_redeem") |

### Methods
- **setRegistrationPoints**(`int points`) -> `void`
- **getRegistrationPoints**(``) -> `int`
- **getPointExpiryDays**(``) -> `int`
- **setPointExpiryDays**(`int pointExpiryDays`) -> `void`
- **getTenantId**(``) -> `Long`
- **setTenantId**(`Long tenantId`) -> `void`
- **getEarningRate**(``) -> `float`
- **setEarningRate**(`float earningRate`) -> `void`
- **getRedeemRate**(``) -> `float`
- **setRedeemRate**(`float redeemRate`) -> `void`
- **getSignUpPoints**(``) -> `int`
- **setSignUpPoints**(`int signUpPoints`) -> `void`
- **getReviewPoints**(``) -> `int`
- **setReviewPoints**(`int reviewPoints`) -> `void`
- **getMinPointsToRedeem**(``) -> `int`
- **setMinPointsToRedeem**(`int minPointsToRedeem`) -> `void`

---

## Entity: Setting
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\Setting.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `key` | @Column(name = "`key`", nullable = false, length = 128) |
| `String` | `value` | @Column(nullable = false, length = 2024) |
| `SettingCategory` | `category` | @Column(length = 45, nullable = false)<br>@Enumerated(EnumType.STRING) |

### Methods
- **getKey**(``) -> `String`
- **setKey**(`String key`) -> `void`
- **getValue**(``) -> `String`
- **setValue**(`String value`) -> `void`
- **getCategory**(``) -> `SettingCategory`
- **setCategory**(`SettingCategory category`) -> `void`
- **hashCode**(``) -> `int`
- **equals**(`Object obj`) -> `boolean`
- **toString**(``) -> `String`

---

## Entity: SettingBag
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\SettingBag.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `final` | `List` | - |

### Methods
- **get**(`String key`) -> `Setting`
- **getValue**(`String key`) -> `String`
- **update**(`String key, String value`) -> `void`
- **list**(``) -> `List<Setting>`
- **getAllSettings**(``) -> `List<Setting>`

---

## Entity: ThemeDTO
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\ThemeDTO.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `primaryColor` | - |
| `String` | `secondaryColor` | - |
| `String` | `headerBg` | - |
| `String` | `headerColor` | - |
| `String` | `footerBg` | - |
| `String` | `footerColor` | - |
| `String` | `fontFamily` | - |
| `String` | `fontSize` | - |
| `String` | `fontWeight` | - |

### Methods
- **getPrimaryColor**(``) -> `String`
- **setPrimaryColor**(`String primaryColor`) -> `void`
- **getSecondaryColor**(``) -> `String`
- **setSecondaryColor**(`String secondaryColor`) -> `void`
- **getHeaderBg**(``) -> `String`
- **setHeaderBg**(`String headerBg`) -> `void`
- **getHeaderColor**(``) -> `String`
- **setHeaderColor**(`String headerColor`) -> `void`
- **getFooterBg**(``) -> `String`
- **setFooterBg**(`String footerBg`) -> `void`
- **getFooterColor**(``) -> `String`
- **setFooterColor**(`String footerColor`) -> `void`
- **getFontFamily**(``) -> `String`
- **setFontFamily**(`String fontFamily`) -> `void`
- **getFontSize**(``) -> `String`
- **setFontSize**(`String fontSize`) -> `void`
- **getFontWeight**(``) -> `String`
- **setFontWeight**(`String fontWeight`) -> `void`

---

## Entity: State
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\state\State.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | @Column(nullable = false, length = 45) |
| `Country` | `country` | @JoinColumn(name = "country_id")<br>@ManyToOne |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getCountry**(``) -> `Country`
- **setCountry**(`Country country`) -> `void`
- **toString**(``) -> `String`

---

## Entity: StateDTO
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\state\StateDTO.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | - |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`

---

## Entity: Country
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\state\Country\Country.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | @Column(nullable = false, length = 45) |
| `String` | `code` | @Column(nullable = false, length = 5) |
| `Set<State>` | `states` | @OneToMany(mappedBy = "country") |
| `Set<Customer>` | `customers` | @OneToMany(mappedBy = "country") |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getCode**(``) -> `String`
- **setCode**(`String code`) -> `void`
- **toString**(``) -> `String`

---

## Entity: Currency
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\subsetting\Currency.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | @Column(nullable = false, length = 64) |
| `String` | `symbol` | @Column(nullable = false, length = 3) |
| `String` | `code` | @Column(nullable = false, length = 4) |

### Methods
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getSymbol**(``) -> `String`
- **setSymbol**(`String symbol`) -> `void`
- **getCode**(``) -> `String`
- **setCode**(`String code`) -> `void`
- **toString**(``) -> `String`

---

## Entity: IdBasedEntity
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\setting\subsetting\IdBasedEntity.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|

### Methods
- **getId**(``) -> `Integer`
- **setId**(`Integer id`) -> `void`

---

## Entity: ShippingRate
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\shipping\ShippingRate.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `float` | `rate` | - |
| `int` | `days` | - |
| `String` | `stateName` | @Column(name = "state") |
| `boolean` | `codSupported` | @Column(name = "cod_supported") |
| `Country` | `country` | @JoinColumn(name = "country_id")<br>@ManyToOne |

### Methods
- **getStateName**(``) -> `String`
- **setStateName**(`String stateName`) -> `void`
- **getRate**(``) -> `float`
- **setRate**(`float rate`) -> `void`
- **getDays**(``) -> `int`
- **setDays**(`int days`) -> `void`
- **isCodSupported**(``) -> `boolean`
- **setCodSupported**(`boolean codSupported`) -> `void`
- **getCountry**(``) -> `Country`
- **setCountry**(`Country country`) -> `void`
- **toString**(``) -> `String`
- **hashCode**(``) -> `int`
- **equals**(`Object obj`) -> `boolean`

---

## Entity: CartItem
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\shoppingCart\CartItem.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Customer` | `customer` | @JoinColumn(name = "customer_id")<br>@ManyToOne |
| `Product` | `product` | @JoinColumn(name = "product_id")<br>@ManyToOne |
| `int` | `quantity` | - |
| `float` | `shippingCost` | @Transient |

### Methods
- **getCustomer**(``) -> `Customer`
- **setCustomer**(`Customer customer`) -> `void`
- **getProduct**(``) -> `Product`
- **setProduct**(`Product product`) -> `void`
- **getQuantity**(``) -> `int`
- **setQuantity**(`int quantity`) -> `void`
- **toString**(``) -> `String`
- **getSubtotal**(``) -> `float`
- **getShippingCost**(``) -> `float`
- **setShippingCost**(`float shippingCost`) -> `void`

---

## Entity: Tenant
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\tenant\Tenant.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `String` | `name` | @Column(nullable = false, unique = true, length = 45) |
| `String` | `code` | @Column(nullable = false, unique = true, length = 45) |
| `TenantStatus` | `status` | @Enumerated(EnumType.STRING)<br>@Column(nullable = false) |
| `Date` | `createdAt` | @Column(name = "created_at", nullable = false, updatable = false) |
| `boolean` | `active` | @Column(name = "active", nullable = false) |
| `int` | `level` | @Column(name = "level", nullable = false) |
| `Tenant` | `parent` | @JoinColumn(name = "parent_id")<br>@ManyToOne(fetch = FetchType.LAZY) |

### Methods
- **isActive**(``) -> `boolean`
- **setActive**(`boolean active`) -> `void`
- **getId**(``) -> `Long`
- **setId**(`Long id`) -> `void`
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getCode**(``) -> `String`
- **setCode**(`String code`) -> `void`
- **getStatus**(``) -> `TenantStatus`
- **setStatus**(`TenantStatus status`) -> `void`
- **getCreatedAt**(``) -> `Date`
- **setCreatedAt**(`Date createdAt`) -> `void`
- **getLevel**(``) -> `int`
- **setLevel**(`int level`) -> `void`
- **getParent**(``) -> `Tenant`
- **setParent**(`Tenant parent`) -> `void`
- **setChildren**(`java.util.Set<Tenant> children`) -> `void`
- **toString**(``) -> `String`

---

## Entity: VisitorSession
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\tracking\VisitorSession.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `tenantId` | @Column(name = "tenant_id", updatable = false) |
| `String` | `sessionId` | @Column(name = "session_id", length = 128, nullable = false, unique = true) |
| `String` | `ipAddress` | @Column(name = "ip_address", length = 45) |
| `String` | `userAgent` | @Column(name = "user_agent", length = 255) |
| `String` | `deviceType` | @Column(name = "device_type", length = 20) |
| `Date` | `startTime` | @Column(name = "start_time")<br>@Column(name = "device_type", length = 20) |
| `Date` | `lastActiveTime` | @Column(name = "last_active_time") |
| `String` | `totalDurationDisplay` | @Column(name = "total_duration_display") |
| `int` | `pageViews` | @Column(name = "page_views")<br>@Column(name = "total_duration_display") |
| `Integer` | `customerId` | @Column(name = "customer_id") |

### Methods
- **getTenantId**(``) -> `Long`
- **setTenantId**(`Long tenantId`) -> `void`
- **getSessionId**(``) -> `String`
- **setSessionId**(`String sessionId`) -> `void`
- **getIpAddress**(``) -> `String`
- **setIpAddress**(`String ipAddress`) -> `void`
- **getUserAgent**(``) -> `String`
- **setUserAgent**(`String userAgent`) -> `void`
- **getDeviceType**(``) -> `String`
- **setDeviceType**(`String deviceType`) -> `void`
- **getStartTime**(``) -> `Date`
- **setStartTime**(`Date startTime`) -> `void`
- **getLastActiveTime**(``) -> `Date`
- **setLastActiveTime**(`Date lastActiveTime`) -> `void`
- **getTotalDurationDisplay**(``) -> `String`
- **setTotalDurationDisplay**(`String totalDurationDisplay`) -> `void`
- **getPageViews**(``) -> `int`
- **setPageViews**(`int pageViews`) -> `void`
- **incrementPageViews**(``) -> `void`
- **getCustomerId**(``) -> `Integer`
- **setCustomerId**(`Integer customerId`) -> `void`
- **getDurationInSeconds**(``) -> `long`

---

## Entity: Role
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\users\Role.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `name` | @Column(name = "name", length = 40, nullable = false, unique = false) |
| `String` | `descrption` | @Column(name = "descrption", length = 150, nullable = false) |
| `Set<RolePermission>` | `permissions` | @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true) |

### Methods
- **getId**(``) -> `Integer`
- **getName**(``) -> `String`
- **setName**(`String name`) -> `void`
- **getDescrption**(``) -> `String`
- **setDescrption**(`String descrption`) -> `void`
- **equals**(`Object o`) -> `boolean`
- **hashCode**(``) -> `int`
- **getPermissions**(``) -> `Set<RolePermission>`
- **setPermissions**(`Set<RolePermission> permissions`) -> `void`
- **hasPermission**(`String permissionName`) -> `boolean`
- **toString**(``) -> `String`

---

## Entity: RolePermission
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\users\RolePermission.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `Role` | `role` | @JoinColumn(name = "role_id", nullable = false)<br>@ManyToOne(fetch = FetchType.LAZY) |
| `String` | `permission` | @Column(nullable = false, length = 100) |
| `String` | `scope` | @Column(length = 50) |

### Methods
- **getId**(``) -> `Long`
- **setId**(`Long id`) -> `void`
- **getRole**(``) -> `Role`
- **setRole**(`Role role`) -> `void`
- **getPermission**(``) -> `String`
- **setPermission**(`String permission`) -> `void`
- **getScope**(``) -> `String`
- **setScope**(`String scope`) -> `void`
- **toString**(``) -> `String`

---

## Entity: User
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\users\User.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `String` | `email` | @Column(name = "email", length = 85, nullable = false) |
| `String` | `password` | @Column(name = "password", length = 85, nullable = false) |
| `String` | `firstName` | @Column(name = "first_name", length = 85, nullable = false) |
| `String` | `lastName` | @Column(name = "last_name", length = 85, nullable = false) |
| `boolean` | `enabled` | @Column(name = "enabled") |
| `String` | `user_bio` | @Column(name = "user_bio") |
| `Set<Role>` | `roles` | @JsonIgnore<br>@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))<br>@ManyToMany(fetch = FetchType.EAGER) |
| `Set<UserTenant>` | `tenants` | @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) |
| `String` | `photos` | @Column(name = "photos", length = 64) |

### Methods
- **getUser_bio**(``) -> `String`
- **setUser_bio**(`String user_bio`) -> `void`
- **setLastLoginTime**(`java.util.Date lastLoginTime`) -> `void`
- **getRoles**(``) -> `Set<Role>`
- **setRoles**(`Set<Role> roles`) -> `void`
- **addRole**(`Role role`) -> `void`
- **isEnabled**(``) -> `boolean`
- **setEnabled**(`boolean enabled`) -> `void`
- **getEmail**(``) -> `String`
- **setEmail**(`String email`) -> `void`
- **getPassword**(``) -> `String`
- **setPassword**(`String password`) -> `void`
- **getfirstName**(``) -> `String`
- **setFirstName**(`String firstName`) -> `void`
- **getLastName**(``) -> `String`
- **setLastName**(`String lastName`) -> `void`
- **toString**(``) -> `String`
- **getImagePath**(``) -> `String`
- **getPhotos**(``) -> `String`
- **setPhotos**(`String photos`) -> `void`
- **getImageDir**(``) -> `String`
- **getFullName**(``) -> `String`
- **hasRole**(`String roleName`) -> `boolean`
- **getTenants**(``) -> `Set<UserTenant>`
- **setTenants**(`Set<UserTenant> tenants`) -> `void`
- **addTenant**(`com.onlineStoreCom.entity.tenant.Tenant tenant`) -> `void`

---

## Entity: UserTenant
**File:** `e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity\users\UserTenant.java`

### Data Fields
| Type | Name | Details/Constraints |
|---|---|---|
| `Long` | `id` | @GeneratedValue(strategy = GenerationType.IDENTITY)<br>@Id |
| `User` | `user` | @JoinColumn(name = "user_id", nullable = false)<br>@ManyToOne(fetch = FetchType.LAZY) |
| `Tenant` | `tenant` | @JoinColumn(name = "tenant_id", nullable = false)<br>@ManyToOne(fetch = FetchType.LAZY) |
| `Role` | `role` | - |

### Methods
- **getId**(``) -> `Long`
- **setId**(`Long id`) -> `void`
- **getUser**(``) -> `User`
- **setUser**(`User user`) -> `void`
- **getTenant**(``) -> `Tenant`
- **setTenant**(`Tenant tenant`) -> `void`
- **equals**(`Object o`) -> `boolean`
- **hashCode**(``) -> `int`

---
