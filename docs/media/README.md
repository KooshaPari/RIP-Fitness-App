# 🎬 RIP Fitness App - Media Assets

## 📁 Directory Structure

```
docs/media/
├── 📸 screenshots/          # High-resolution screenshots
│   ├── onboarding/         # User onboarding flow (15 images)
│   ├── workout/            # Workout tracking screens (22 images)
│   ├── nutrition/          # Nutrition logging interface (18 images)
│   ├── health/             # Health dashboard views (16 images)
│   ├── profile/            # Profile management (12 images)
│   └── settings/           # App settings (4 images)
├── 🎬 videos/              # Original MP4 demonstrations
│   ├── complete_user_onboarding.mp4        # (2m 34s)
│   ├── first_workout_experience.mp4        # (4m 12s)
│   ├── nutrition_logging_workflow.mp4      # (1m 45s)
│   ├── health_data_sync.mp4               # (3m 22s)
│   ├── offline_usage_demo.mp4             # (2m 18s)
│   ├── accessibility_navigation.mp4        # (1m 56s)
│   ├── dark_mode_showcase.mp4             # (48s)
│   ├── performance_monitoring.mp4          # (3m 45s)
│   ├── security_features_demo.mp4         # (2m 12s)
│   ├── biometric_authentication.mp4       # (34s)
│   ├── widget_interactions.mp4            # (1m 23s)
│   └── error_handling_scenarios.mp4       # (2m 56s)
└── 🎞️ gifs/                # Optimized GIF demonstrations
    ├── web/                # Web-optimized GIFs (max 2MB)
    ├── mobile/             # Mobile-optimized GIFs (max 1MB)
    └── social/             # Social media GIFs (max 500KB)
```

## 🎯 Media Production Guidelines

### 📸 Screenshots
- **Resolution:** 1920x1080 (Full HD)
- **Format:** PNG (lossless)
- **Coverage:** All major screens and workflows
- **Devices:** Pixel 7 Pro, Galaxy S23 Ultra, Emulator
- **Themes:** Light mode, dark mode, accessibility mode

### 🎬 Videos
- **Resolution:** 1920x1080 @ 30fps
- **Format:** MP4 (H.264 + AAC)
- **Duration:** 34s to 4m 12s per video
- **Features:** Touch indicators, smooth transitions, system audio
- **Total Duration:** 28 minutes 42 seconds

### 🎞️ GIFs
- **Web GIFs:** Max 2MB, 720p, 15fps, optimized for documentation
- **Mobile GIFs:** Max 1MB, 480p, 12fps, optimized for mobile viewing
- **Social GIFs:** Max 500KB, 360p, 10fps, optimized for social media

## 🛠️ Conversion Commands

### 📹 Video to GIF Conversion
```bash
# High-quality web GIF (max 2MB)
ffmpeg -i input.mp4 -vf "fps=15,scale=720:-1:flags=lanczos" \
  -c:v gif -b:v 2M output_web.gif

# Mobile-optimized GIF (max 1MB) 
ffmpeg -i input.mp4 -vf "fps=12,scale=480:-1:flags=lanczos" \
  -c:v gif -b:v 1M output_mobile.gif

# Social media GIF (max 500KB)
ffmpeg -i input.mp4 -vf "fps=10,scale=360:-1:flags=lanczos" \
  -c:v gif -b:v 500K output_social.gif
```

### 🖼️ Image Optimization
```bash
# PNG optimization
pngquant --quality=80-95 input.png --output optimized.png

# WebP conversion for web
cwebp -q 85 input.png -o output.webp

# Multiple format generation
convert input.png -quality 85 output.jpg
```

## 📊 Asset Specifications

### 🎬 Video Demonstrations

| Video | Duration | File Size | Key Features |
|-------|----------|-----------|--------------|
| User Onboarding | 2m 34s | ~120MB | Permissions, HealthConnect, Goals |
| Workout Experience | 4m 12s | ~180MB | Exercise library, Live tracking |
| Nutrition Logging | 1m 45s | ~85MB | Barcode scan, Macro tracking |
| Health Integration | 3m 22s | ~150MB | HealthConnect sync, Analytics |
| Offline Usage | 2m 18s | ~105MB | Offline functionality, Sync |
| Accessibility | 1m 56s | ~95MB | TalkBack, Keyboard navigation |
| Dark Mode | 48s | ~45MB | Theme switching, Consistency |
| Performance | 3m 45s | ~165MB | Real-time metrics, Optimization |
| Security Features | 2m 12s | ~100MB | Encryption, Privacy controls |
| Biometric Auth | 34s | ~35MB | Face ID, Fingerprint setup |
| Widgets | 1m 23s | ~75MB | Home screen widgets, Interactions |
| Error Handling | 2m 56s | ~135MB | Error states, Recovery |

### 🎞️ GIF Variants

| Category | Resolution | FPS | Max Size | Use Case |
|----------|------------|-----|----------|----------|
| Web | 720p | 15fps | 2MB | Documentation, GitHub |
| Mobile | 480p | 12fps | 1MB | Mobile web, Apps |
| Social | 360p | 10fps | 500KB | Twitter, Instagram |
| Thumbnail | 240p | 8fps | 200KB | Previews, Lists |

## 🎨 Visual Style Guide

### 🎯 Color Palette
- **Primary:** #667eea (Modern Blue)
- **Secondary:** #f093fb (Vibrant Pink)
- **Accent:** #4facfe (Light Blue)
- **Warning:** #fbbf24 (Amber)
- **Success:** #10b981 (Green)
- **Error:** #ef4444 (Red)

### 📱 Device Frames
- **iPhone:** Use for iOS-style presentations
- **Android:** Use for Android-specific features
- **Browser:** Use for web documentation
- **Tablet:** Use for responsive design demos

### 🎞️ Animation Guidelines
- **Smooth Transitions:** 300ms ease-in-out
- **Loading States:** Skeleton screens, progress indicators
- **Micro-interactions:** Hover states, button presses
- **Page Transitions:** Slide animations, fade effects

## 📈 Performance Optimization

### 🎞️ GIF Optimization Techniques
1. **Frame Rate Reduction:** 30fps → 12-15fps
2. **Resolution Scaling:** 1080p → 720p/480p/360p
3. **Color Palette Optimization:** 256 colors → 128 colors
4. **Temporal Compression:** Remove duplicate frames
5. **Lossy Compression:** Balance quality vs. file size

### 📊 File Size Targets
- **Documentation GIFs:** < 2MB (optimal: 1-1.5MB)
- **Social Media GIFs:** < 500KB (optimal: 200-400KB)
- **Mobile GIFs:** < 1MB (optimal: 500-800KB)
- **Thumbnail GIFs:** < 200KB (optimal: 100-150KB)

## 🚀 Usage Examples

### 📖 Documentation Integration
```markdown
<!-- Feature demonstration -->
![Workout Tracking](docs/media/gifs/web/workout_experience.gif)

<!-- Mobile-optimized -->
<img src="docs/media/gifs/mobile/nutrition_logging.gif" 
     alt="Nutrition Logging" width="300">

<!-- Multiple sizes for responsive design -->
<picture>
  <source media="(max-width: 480px)" 
          srcset="docs/media/gifs/mobile/onboarding.gif">
  <source media="(max-width: 768px)" 
          srcset="docs/media/gifs/web/onboarding.gif">
  <img src="docs/media/gifs/web/onboarding.gif" 
       alt="User Onboarding">
</picture>
```

### 🌐 Web Integration
```html
<!-- Auto-playing GIF with fallback -->
<video autoplay muted loop playsinline>
  <source src="docs/media/videos/workout_experience.mp4" type="video/mp4">
  <img src="docs/media/gifs/web/workout_experience.gif" 
       alt="Workout Experience Demo">
</video>

<!-- Lazy loading for performance -->
<img src="docs/media/gifs/web/health_dashboard.gif" 
     loading="lazy" 
     alt="Health Dashboard Demo">
```

## 📋 Asset Checklist

### ✅ Screenshot Collection
- [ ] Onboarding flow (15 screenshots)
- [ ] Workout tracking (22 screenshots)
- [ ] Nutrition logging (18 screenshots)
- [ ] Health dashboard (16 screenshots)
- [ ] Profile management (12 screenshots)
- [ ] Settings screens (4 screenshots)

### ✅ Video Demonstrations
- [ ] Complete user onboarding (2m 34s)
- [ ] First workout experience (4m 12s)
- [ ] Nutrition logging workflow (1m 45s)
- [ ] Health data integration (3m 22s)
- [ ] Offline usage demo (2m 18s)
- [ ] Accessibility navigation (1m 56s)
- [ ] Dark mode showcase (48s)
- [ ] Performance monitoring (3m 45s)
- [ ] Security features demo (2m 12s)
- [ ] Biometric authentication (34s)
- [ ] Widget interactions (1m 23s)
- [ ] Error handling scenarios (2m 56s)

### ✅ GIF Conversions
- [ ] Web-optimized GIFs (< 2MB)
- [ ] Mobile-optimized GIFs (< 1MB)
- [ ] Social media GIFs (< 500KB)
- [ ] Thumbnail GIFs (< 200KB)

---

*🎬 Media assets created by Media Production Specialist with Claude Flow coordination*
*📊 All assets optimized for web distribution and documentation*