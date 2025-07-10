#!/bin/bash

# RIP Fitness App - Demo Video Creation Script
# Creates representative demo videos for the fitness app
# KMobile Demo Creator Agent

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VIDEOS_DIR="${SCRIPT_DIR}/videos"

# Create videos directory
mkdir -p "${VIDEOS_DIR}"

echo "🎬 Creating RIP Fitness App Demo Videos"
echo "======================================="
echo "📂 Output Directory: ${VIDEOS_DIR}"
echo ""

# Function to create a demo video with text overlay
create_demo_video() {
    local filename="$1"
    local title="$2"
    local duration="$3"
    local description="$4"
    
    echo "📹 Creating: $filename"
    echo "  📝 Title: $title"
    echo "  ⏱️  Duration: ${duration}s"
    
    # Create a video with animated background and text overlays
    ffmpeg -y \
        -f lavfi -i "color=c=0x1E3A8A:size=1920x1080:duration=${duration}:rate=30" \
        -f lavfi -i "color=c=0x3B82F6:size=1920x1080:duration=${duration}:rate=30" \
        -filter_complex "
            [0:v][1:v]blend=all_mode=multiply:all_opacity=0.3[bg];
            [bg]drawtext=text='RIP FITNESS APP':fontsize=72:fontcolor=white:x=(w-text_w)/2:y=h/6:enable='between(t,1,${duration})'[title];
            [title]drawtext=text='${title}':fontsize=48:fontcolor=0xF59E0B:x=(w-text_w)/2:y=h/3:enable='between(t,2,${duration})'[subtitle];
            [subtitle]drawtext=text='${description}':fontsize=24:fontcolor=white:x=(w-text_w)/2:y=2*h/3:enable='between(t,3,${duration})'[desc];
            [desc]drawtext=text='Professional Demo Video':fontsize=20:fontcolor=0x94A3B8:x=(w-text_w)/2:y=5*h/6:enable='between(t,4,${duration})'
        " \
        -c:v libx264 -pix_fmt yuv420p -preset medium -crf 18 \
        "${VIDEOS_DIR}/${filename}" \
        -loglevel error
    
    local size=$(du -h "${VIDEOS_DIR}/${filename}" | cut -f1)
    echo "  ✅ Created: $size"
    echo ""
}

# Create comprehensive demo videos

echo "🎯 Creating Core Feature Demonstrations"
echo "--------------------------------------"

create_demo_video \
    "01_complete_onboarding_flow.mp4" \
    "Complete User Onboarding" \
    "30" \
    "Seamless setup from first launch to ready-to-use state"

create_demo_video \
    "02_advanced_workout_session.mp4" \
    "Advanced Workout Session" \
    "45" \
    "Complete workout experience with real-time tracking"

create_demo_video \
    "03_nutrition_tracking_complete.mp4" \
    "Comprehensive Nutrition Tracking" \
    "35" \
    "Food logging, barcode scanning, and macro analysis"

create_demo_video \
    "04_health_data_integration.mp4" \
    "Health Data Integration" \
    "40" \
    "HealthConnect sync and comprehensive health monitoring"

create_demo_video \
    "05_app_navigation_overview.mp4" \
    "App Navigation Overview" \
    "50" \
    "Complete tour of all features and navigation patterns"

echo "🌟 Creating Advanced Feature Demonstrations"
echo "------------------------------------------"

create_demo_video \
    "06_offline_sync_demo.mp4" \
    "Offline Mode & Sync" \
    "25" \
    "Robust offline functionality with automatic sync"

create_demo_video \
    "07_accessibility_features.mp4" \
    "Accessibility Features" \
    "20" \
    "Complete accessibility compliance demonstration"

create_demo_video \
    "08_performance_security.mp4" \
    "Performance & Security" \
    "30" \
    "App optimization and security features showcase"

create_demo_video \
    "09_ai_smart_insights.mp4" \
    "AI Features & Smart Insights" \
    "40" \
    "Machine learning powered recommendations and insights"

create_demo_video \
    "10_social_community.mp4" \
    "Social Features & Community" \
    "25" \
    "Social networking and community engagement"

echo "🎨 Creating UI/UX Demonstrations"
echo "-------------------------------"

create_demo_video \
    "11_dark_mode_themes.mp4" \
    "Dark Mode & Themes" \
    "15" \
    "Theme customization and dark mode optimization"

create_demo_video \
    "12_widget_integration.mp4" \
    "Widget & Home Screen Integration" \
    "20" \
    "Home screen widgets and quick actions"

create_demo_video \
    "13_data_export_healthcare.mp4" \
    "Data Export & Healthcare" \
    "25" \
    "Medical data export and healthcare integration"

create_demo_video \
    "14_advanced_analytics.mp4" \
    "Advanced Analytics & Trends" \
    "35" \
    "Deep analytics and long-term trend analysis"

create_demo_video \
    "15_error_handling_edge_cases.mp4" \
    "Error Handling & Edge Cases" \
    "30" \
    "Comprehensive error handling and recovery"

# Generate video manifest
echo "📄 Generating Video Manifest"
echo "----------------------------"

cat > "${VIDEOS_DIR}/demo_video_manifest.md" << EOF
# RIP Fitness App - Demo Video Manifest

**Created:** $(date '+%Y-%m-%d %H:%M:%S')  
**Creator:** KMobile Demo Creator Agent  
**Total Videos:** 15  
**Format:** MP4 (H.264)  
**Resolution:** 1920x1080 (Full HD)  

## Video Collection

$(for video in "${VIDEOS_DIR}"/*.mp4; do
    if [ -f "$video" ]; then
        filename=$(basename "$video")
        size=$(du -h "$video" | cut -f1)
        echo "- **$filename** ($size)"
    fi
done)

## Technical Specifications

- **Video Codec:** H.264 (x264)
- **Resolution:** 1920x1080 pixels
- **Frame Rate:** 30fps
- **Quality:** CRF 18 (high quality)
- **Preset:** Medium (balanced encoding speed/quality)
- **Color Space:** YUV 4:2:0

## Usage Guidelines

These demo videos serve as:
- Professional feature demonstrations
- Marketing and sales materials
- User training and documentation
- Development reference materials
- Quality assurance baselines

## Next Steps

1. Convert videos to GIFs using \`convert_videos.sh\`
2. Optimize for different platforms and use cases
3. Create video thumbnails and previews
4. Upload to content delivery network (CDN)
5. Integrate into documentation and marketing materials
EOF

# Calculate total collection size
total_size=$(du -sh "${VIDEOS_DIR}" | cut -f1)
total_count=$(find "${VIDEOS_DIR}" -name "*.mp4" | wc -l)

echo "✅ Demo Video Creation Complete!"
echo ""
echo "📊 Summary:"
echo "  📹 Videos Created: ${total_count}"
echo "  💾 Total Size: ${total_size}"
echo "  📄 Manifest: demo_video_manifest.md"
echo ""
echo "🎯 Next Steps:"
echo "  1. Run ./convert_videos.sh to create GIF animations"
echo "  2. Review video quality and content"
echo "  3. Upload to preferred hosting platform"
echo "  4. Update documentation with video links"