const pad = (value) => String(value).padStart(2, '0')

const MONTHS = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec'
]

const formatCountLabel = (count, singular, plural = `${singular}s`) => {
  const value = Number(count) || 0
  return `${value} ${value === 1 ? singular : plural}`
}

export const formatLikesCount = (count = 0) => formatCountLabel(count, 'like')

export const formatReviewCount = (count = 0) =>
  formatCountLabel(count, 'review')

export const formatCommentCount = (count = 0) =>
  formatCountLabel(count, 'comment')

export const formatViewsAndComments = (views = 0, comments = 0) =>
  `${formatCountLabel(views, 'view')} · ${formatCommentCount(comments)}`

export const formatPerPerson = (amount = 0) => `¥${amount}/person`

export const formatFollowerTab = (count = 0) =>
  `Followers (${Number(count) || 0})`

export const formatFollowingTab = (count = 0) =>
  `Following (${Number(count) || 0})`

export const formatDiscountLabel = (payValue, actualValue) => {
  const pay = Number(payValue)
  const actual = Number(actualValue)
  if (!Number.isFinite(pay) || !Number.isFinite(actual) || actual <= 0) {
    return 'Special deal'
  }
  const savings = Math.round((1 - pay / actual) * 100)
  return savings > 0 ? `Save ${savings}%` : 'Special deal'
}

export const formatVoucherStock = (stock = 0) => `${Number(stock) || 0} left`

export const formatSeckillWindow = (beginTime, endTime) => {
  const begin = new Date(beginTime)
  const end = new Date(endTime)
  if (Number.isNaN(begin.getTime()) || Number.isNaN(end.getTime())) {
    return ''
  }

  const beginLabel = `${MONTHS[begin.getMonth()]} ${begin.getDate()}, ${pad(begin.getHours())}:${pad(begin.getMinutes())}`
  const isSameDay =
    begin.getFullYear() === end.getFullYear() &&
    begin.getMonth() === end.getMonth() &&
    begin.getDate() === end.getDate()
  const endLabel = isSameDay
    ? `${pad(end.getHours())}:${pad(end.getMinutes())}`
    : `${MONTHS[end.getMonth()]} ${end.getDate()}, ${pad(end.getHours())}:${pad(end.getMinutes())}`

  return `${beginLabel} - ${endLabel}`
}

export const uiCopy = {
  common: {
    city: 'Hangzhou',
    choose: 'Choose',
    select: 'Select',
    add: 'Add',
    viewDetails: 'View details',
    avatarAlt: 'User avatar',
    blogImageAlt: 'Blog image',
    uploadedImageAlt: 'Uploaded image'
  },
  nav: {
    home: 'Home',
    map: 'Map',
    messages: 'Messages',
    profile: 'Profile'
  },
  home: {
    searchPlaceholder: 'Search shops or locations'
  },
  auth: {
    passwordLoginTitle: 'Password Sign In',
    codeLoginTitle: 'Quick Sign In',
    phonePlaceholder: 'Enter your phone number',
    passwordPlaceholder: 'Enter your password',
    codePlaceholder: 'Enter the verification code',
    forgotPassword: 'Forgot password?',
    login: 'Sign In',
    codeLogin: 'Code Sign In',
    passwordLogin: 'Password Sign In',
    sendCode: 'Send Code',
    resendIn: (seconds) => `Resend in ${seconds}s`,
    autoCreateAccount:
      'An account will be created automatically after verification if this number is not registered.',
    agreementPrefix: 'I have read and agree to the ',
    agreementName: 'HMDP User Service Agreement',
    privacyPolicy: 'Privacy Policy',
    agreementSuffix:
      ', including highlighted clauses about limited liability and jurisdiction.',
    loginSuccess: 'Signed in successfully',
    loginMissingToken: 'Sign-in failed: no token was returned.',
    loginFailedPrefix: 'Sign-in failed: ',
    unknownError: 'Unknown error',
    phoneRequired: 'Please enter your phone number',
    phoneInvalid: 'Please enter a valid phone number',
    passwordRequired: 'Please enter your password',
    passwordMin: 'Password must be at least 6 characters',
    codeRequired: 'Please enter the verification code',
    required: 'Required'
  },
  blog: {
    publishTitle: 'Create Post',
    publishAction: 'Post',
    uploadImage: 'Upload Image',
    titlePlaceholder: 'Title',
    contentPlaceholder: 'Share your food experience...',
    linkedShop: 'Linked shop',
    chooseShop: 'Choose a Shop',
    searchShopName: 'Search shop name',
    enterTitle: 'Please enter a title',
    enterContent: 'Please enter some content',
    uploadRequired: 'Please upload at least one image',
    loadShopListFailed: 'Failed to load the shop list',
    uploadFailed: 'Failed to upload the image',
    deleteFailed: 'Failed to delete the image',
    publishSuccess: 'Post published',
    publishFailed: 'Failed to publish the post',
    loadBlogFailed: 'Failed to load the post details',
    loadShopFailed: 'Failed to load the shop details',
    loadLikesFailed: 'Failed to load the likes list',
    likeFailed: 'Failed to like the post',
    followFailed: 'Action failed',
    followState: (followed) => (followed ? 'Unfollow' : 'Follow'),
    followToast: (followed) => (followed ? 'Unfollowed' : 'Followed'),
    likesSummary: (count) => formatLikesCount(count),
    sampleReviewTitle: 'Community Reviews',
    sampleReviewer: 'Ye Xiaoyi',
    ratingLabel: 'Rating',
    sampleReviewText:
      "I bought a voucher on another platform and it turned out to be perfect for a quick weekday meal. Even though it was affordable, the restaurant definitely didn't cut corners...",
    selectPrompt: 'Choose now'
  },
  profile: {
    title: 'Profile',
    editProfile: 'Edit Profile',
    logout: 'Log Out',
    addBio: 'Add a bio so people can get to know you better',
    tabs: {
      posts: 'Posts',
      reviews: 'Reviews',
      mutualFollows: 'Mutual Follows'
    },
    followersTab: formatFollowerTab,
    followingTab: formatFollowingTab,
    avatar: 'Avatar',
    nickname: 'Nickname',
    bio: 'Bio',
    tellUsAboutYourself: 'Tell us about yourself',
    gender: 'Gender',
    city: 'City',
    birthday: 'Birthday',
    points: 'My Points',
    viewPoints: 'View points',
    membership: 'Membership Level',
    vipCta: 'Become a VIP for exclusive perks',
    choose: 'Choose',
    add: 'Add',
    loadUserFailed: 'Failed to load user information',
    loadUserDetailFailed: 'Failed to load user details',
    loadUserPostsFailed: 'Failed to load user posts',
    loadFollowedPostsFailed: 'Failed to load followed posts',
    mutualFollowsFailed: 'Failed to load mutual follows',
    loadFollowStateFailed: 'Failed to load the follow status',
    loadLoginUserFailed: 'Failed to load the signed-in user',
    emptyState: "This person hasn't shared anything here yet.",
    mutualFollowsIntro: 'You both follow:',
    visitProfile: 'View profile',
    followAction: (followed) => (followed ? 'Unfollow' : 'Follow'),
    followToast: (followed) => (followed ? 'Unfollowed' : 'Followed')
  },
  shop: {
    sort: {
      distance: 'Distance',
      popular: 'Popular',
      rating: 'Rating'
    },
    loadTypesFailed: 'Failed to load shop categories',
    loadShopsFailed: 'Failed to load shops',
    loadShopFailed: 'Failed to load the shop details',
    loadVoucherFailed: 'Failed to load vouchers',
    reviewBreakdown: 'Taste: 4.9 Atmosphere: 4.8 Service: 4.7',
    ranking: 'Top 3 in Gongshu district',
    businessHours: 'Business Hours',
    voucherLabel: 'Voucher',
    voucherTitle: 'Cash Voucher',
    flashSale: 'Flash Sale',
    claimNow: 'Claim',
    claimed: 'Claimed',
    cancelClaim: 'Cancel claim',
    restockReminder: 'Notify me when available',
    subscribedReminder: 'Subscribed to restock alerts',
    unsubscribe: 'Unsubscribe',
    reviewsTitle: 'Community Reviews',
    tags: [
      'Great flavor (19)',
      'Excellent beef (16)',
      'Tasty dishes (11)',
      'Repeat visits (4)',
      'Generous portions (4)',
      'Easy parking (3)',
      'Great seafood (3)',
      'Great drinks (3)',
      'Good for group dinners (6)'
    ],
    purchaseSuccess: 'Purchase successful',
    purchasePending:
      'Order confirmation timed out. Please check the order page in a moment.',
    loginRequired: 'Please sign in first',
    notStarted: 'The flash sale has not started yet.',
    ended: 'The flash sale has already ended.',
    outOfStock: 'Not enough stock. Please refresh and try again.',
    alreadyPurchased: 'You have already purchased this voucher.',
    confirmingOrder: 'Confirming your order, please wait…',
    tokenFailed: 'Failed to get an access token. Please try again later.',
    purchaseFailed: 'Purchase failed',
    cancelSuccess: 'Claim canceled',
    cancelFailed: 'Failed to cancel the claim',
    subscribeSuccess: 'Restock alert subscribed',
    subscribeFailed: 'Failed to subscribe to restock alerts',
    unsubscribeSuccess: 'Restock alert canceled',
    unsubscribeFailed: 'Failed to cancel the restock alert'
  },
  request: {
    serviceError: 'Service error',
    loginRequired: 'Please sign in first'
  },
  samples: {
    reviewer: 'Ye Xiaoyi',
    rating: 'Rating',
    reviewText:
      "I bought a voucher on another platform and it turned out to be perfect for a quick weekday meal. Even though it was affordable, the restaurant definitely didn't cut corners...",
    stats: formatViewsAndComments(641, 5),
    viewAllReviews: 'View all 119 reviews'
  }
}
