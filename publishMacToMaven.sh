#!/bin/bash

./gradlew publishAllPublicationsToMavenRepository -x publishKotlinMultiplatformPublicationToMavenRepository -x publishMetadataPublicationToMavenRepository -x publishJvmPublicationToMavenRepository -x publishAndroidDebugPublicationToMavenRepositor -x publishAndroidReleasePublicationToMavenRepository
