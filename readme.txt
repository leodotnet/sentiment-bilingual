================================================================================
=============================== README GUIDE ===================================
================================================================================

 
 
Configurable input parameters to CRF system are:
 
1) To use Shared or Independent features. Param is:
- SHARE_FEATURES = true means feature-sharing; SHARE_FEATURES = false, which is a default value, means there is no feature-sharing
 
2) To use embedding or no embedding. Param is:
- TargetSentimentGlobal.ENABLE_WORD_EMBEDDING = true/false
 
2.1) If embedding *is* used, then the relevant params are:
- USE_UNKNOWN (USE_UNKNOWN = true means to use word vector corresponding to "</s>" in embedding file when input word is not found in vocabulary)
- ALLOW_MIXCASE_EMBEDDING (ALLOW_MIXCASE_EMBEDDING = true means to make input-word to lowercase when consulting word vector from embedding file; hint: if all words in embedding file are in lower-case, then set ALLOW_MIXCASE_EMBEDDING to true)
- APPEND_SUFFIX (APPEND_SUFFIX = true means to append suffix to input-word when consulting word vector from embedding file; hint: if embedding file uses suffix then APPEND_SUFFIX should be set to true)
 
3) To train using only target language data (translated from source language) while ignoring source language training data. Param is:
- ONLY_TARGET_LANG (ONLY_TARGET_LANG = true means to train strictly using only the translated target language data)
 
================================================================================