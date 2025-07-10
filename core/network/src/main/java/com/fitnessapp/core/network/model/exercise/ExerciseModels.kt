package com.fitnessapp.core.network.model.exercise

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    @SerialName("bodyPart") val bodyPart: String,
    @SerialName("equipment") val equipment: String,
    @SerialName("gifUrl") val gifUrl: String,
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("target") val target: String,
    @SerialName("secondaryMuscles") val secondaryMuscles: List<String>? = null,
    @SerialName("instructions") val instructions: List<String>? = null
)

@Serializable
data class ExerciseDetailed(
    @SerialName("bodyPart") val bodyPart: String,
    @SerialName("equipment") val equipment: String,
    @SerialName("gifUrl") val gifUrl: String,
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("target") val target: String,
    @SerialName("secondaryMuscles") val secondaryMuscles: List<String>,
    @SerialName("instructions") val instructions: List<String>
)

@Serializable
data class ExerciseCategory(
    @SerialName("name") val name: String,
    @SerialName("exercises") val exercises: List<Exercise>
)

@Serializable
data class WorkoutPlan(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("difficulty_level") val difficultyLevel: String, // beginner, intermediate, advanced
    @SerialName("target_body_parts") val targetBodyParts: List<String>,
    @SerialName("exercises") val exercises: List<WorkoutExercise>,
    @SerialName("equipment_needed") val equipmentNeeded: List<String>,
    @SerialName("calories_burned_estimate") val caloriesBurnedEstimate: Int?
)

@Serializable
data class WorkoutExercise(
    @SerialName("exercise") val exercise: Exercise,
    @SerialName("sets") val sets: Int,
    @SerialName("reps") val reps: String, // Can be "10-12" or just "10"
    @SerialName("duration_seconds") val durationSeconds: Int? = null, // For time-based exercises
    @SerialName("rest_seconds") val restSeconds: Int,
    @SerialName("notes") val notes: String? = null,
    @SerialName("order") val order: Int
)

@Serializable
data class ExerciseProgress(
    @SerialName("exercise_id") val exerciseId: String,
    @SerialName("date") val date: String,
    @SerialName("sets_completed") val setsCompleted: Int,
    @SerialName("reps_completed") val repsCompleted: List<Int>,
    @SerialName("weight_used") val weightUsed: List<Double>? = null,
    @SerialName("duration_seconds") val durationSeconds: Int? = null,
    @SerialName("calories_burned") val caloriesBurned: Int? = null,
    @SerialName("difficulty_rating") val difficultyRating: Int? = null, // 1-10 scale
    @SerialName("notes") val notes: String? = null
)

@Serializable
data class MuscleGroup(
    @SerialName("name") val name: String,
    @SerialName("primary_muscles") val primaryMuscles: List<String>,
    @SerialName("secondary_muscles") val secondaryMuscles: List<String>,
    @SerialName("exercises") val exercises: List<Exercise>
)

@Serializable
data class EquipmentType(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("category") val category: String, // cardio, strength, bodyweight, etc.
    @SerialName("exercises") val exercises: List<Exercise>
)