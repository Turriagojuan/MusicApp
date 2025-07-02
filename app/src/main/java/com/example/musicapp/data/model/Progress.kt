package com.example.musicapp.data.model

import com.google.firebase.firestore.PropertyName

data class Progress(
    @get:PropertyName("rhythmModule") @set:PropertyName("rhythmModule")
    var rhythmModule: RhythmModuleProgress = RhythmModuleProgress(),

    @get:PropertyName("staffModule") @set:PropertyName("staffModule")
    var staffModule: StaffModuleProgress = StaffModuleProgress()
)

data class RhythmModuleProgress(
    @get:PropertyName("lesson_1_1_completed") @set:PropertyName("lesson_1_1_completed")
    var lesson_1_1_completed: Boolean = false,

    @get:PropertyName("lesson_1_2_completed") @set:PropertyName("lesson_1_2_completed")
    var lesson_1_2_completed: Boolean = false,

    @get:PropertyName("lesson_1_3_completed") @set:PropertyName("lesson_1_3_completed")
    var lesson_1_3_completed: Boolean = false,

    @get:PropertyName("game_highscore") @set:PropertyName("game_highscore")
    var game_highscore: Long = 0L
)

data class StaffModuleProgress(
    @get:PropertyName("lesson_2_1_completed") @set:PropertyName("lesson_2_1_completed")
    var lesson_2_1_completed: Boolean = false,

    @get:PropertyName("lesson_2_2_completed") @set:PropertyName("lesson_2_2_completed")
    var lesson_2_2_completed: Boolean = false,

    @get:PropertyName("lesson_2_3_completed") @set:PropertyName("lesson_2_3_completed")
    var lesson_2_3_completed: Boolean = false,

    @get:PropertyName("lesson_2_4_completed") @set:PropertyName("lesson_2_4_completed")
    var lesson_2_4_completed: Boolean = false,

    @get:PropertyName("game_highscore") @set:PropertyName("game_highscore")
    var game_highscore: Long = 0L
)