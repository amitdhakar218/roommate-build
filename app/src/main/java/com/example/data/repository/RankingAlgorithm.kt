package com.example.data.repository

import com.example.data.model.PropertyEntity
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Configurable weights and recommendation algorithm for RoomMate
 */
data class RankingWeights(
    val locationWeight: Double = 0.30,
    val budgetWeight: Double = 0.20,
    val ratingWeight: Double = 0.15,
    val reviewsCountWeight: Double = 0.10,
    val verificationWeight: Double = 0.10,
    val freshnessWeight: Double = 0.05,
    val completenessWeight: Double = 0.05,
    val preferenceMatchWeight: Double = 0.05,
    val newListingExplorationBoost: Double = 0.08 // Exploration component for newly listed properties
)

data class UserSearchContext(
    val selectedCity: String = "Gwalior",
    val selectedArea: String = "Thatipur",
    val selectedCollege: String = "Vikrant University",
    val targetMaxRent: Int = 8000,
    val targetMinRent: Int = 2000,
    val preferredPropertyType: String? = null,
    val genderPreference: String? = null,
    val requiredFacilities: List<String> = emptyList()
)

object SmartRankingEngine {
    var config: RankingWeights = RankingWeights()

    fun calculateScore(
        property: PropertyEntity,
        context: UserSearchContext
    ): Double {
        // 1. Location Match (0.0 to 1.0)
        var locationScore = 0.3
        if (property.city.equals(context.selectedCity, ignoreCase = true)) {
            locationScore += 0.3
        }
        if (property.area.equals(context.selectedArea, ignoreCase = true)) {
            locationScore += 0.4
        } else if (property.distanceKm <= 2.0) {
            locationScore += 0.3
        } else if (property.distanceKm <= 5.0) {
            locationScore += 0.15
        }
        if (context.selectedCollege.isNotBlank() &&
            (property.nearCollege.contains(context.selectedCollege, ignoreCase = true) ||
             property.description.contains(context.selectedCollege, ignoreCase = true) ||
             property.title.contains(context.selectedCollege, ignoreCase = true))
        ) {
            locationScore = min(1.0, locationScore + 0.3)
        }
        locationScore = min(1.0, locationScore)

        // 2. Budget Match (0.0 to 1.0)
        val budgetScore: Double = when {
            property.monthlyRent in context.targetMinRent..context.targetMaxRent -> 1.0
            property.monthlyRent < context.targetMinRent -> 0.8
            property.monthlyRent <= context.targetMaxRent * 1.25 -> 0.6
            property.monthlyRent <= context.targetMaxRent * 1.5 -> 0.3
            else -> 0.1
        }

        // 3. Rating Score (0.0 to 1.0)
        val ratingScore = (property.ratingAvg / 5.0).coerceIn(0.0, 1.0)

        // 4. Reviews count (logarithmic/capped normalization to avoid older bloated listings dominating)
        val reviewCountScore = (min(property.reviewsCount, 50).toDouble() / 50.0).coerceIn(0.0, 1.0)

        // 5. Verification
        val verificationScore = if (property.isVerified) 1.0 else 0.4

        // 6. Freshness (Created within last 7 days gets higher score)
        val ageDays = (System.currentTimeMillis() - property.createdAt) / (1000.0 * 60 * 60 * 24)
        val freshnessScore = when {
            ageDays <= 2 -> 1.0
            ageDays <= 7 -> 0.8
            ageDays <= 30 -> 0.5
            else -> 0.3
        }

        // 7. Profile Completeness (has photos, description, facilities, landmark)
        var completeness = 0.4
        if (property.photos.split(",").size >= 3) completeness += 0.2
        if (property.description.length > 50) completeness += 0.2
        if (property.facilities.isNotBlank()) completeness += 0.1
        if (property.landmark.isNotBlank()) completeness += 0.1
        val completenessScore = min(1.0, completeness)

        // 8. User Preference Match
        var preferenceScore = 0.5
        if (context.preferredPropertyType != null && property.propertyType.equals(context.preferredPropertyType, ignoreCase = true)) {
            preferenceScore += 0.3
        }
        if (context.genderPreference != null && (property.genderPreference.equals("Any", ignoreCase = true) || property.genderPreference.equals(context.genderPreference, ignoreCase = true))) {
            preferenceScore += 0.2
        }
        val prefScore = min(1.0, preferenceScore)

        // Weighted sum
        var total = (locationScore * config.locationWeight) +
                (budgetScore * config.budgetWeight) +
                (ratingScore * config.ratingWeight) +
                (reviewCountScore * config.reviewsCountWeight) +
                (verificationScore * config.verificationWeight) +
                (freshnessScore * config.freshnessWeight) +
                (completenessScore * config.completenessWeight) +
                (prefScore * config.preferenceMatchWeight)

        // Exploration boost for fresh listings (gives new owners impressions)
        if (ageDays <= 3 || property.reviewsCount < 5) {
            val randomExploration = Random(property.id.hashCode()).nextDouble(0.02, config.newListingExplorationBoost)
            total += randomExploration
        }

        return total
    }

    fun rankProperties(
        properties: List<PropertyEntity>,
        context: UserSearchContext
    ): List<Pair<PropertyEntity, Double>> {
        return properties
            .map { it to calculateScore(it, context) }
            .sortedByDescending { it.second }
    }
}
