# Tema Yin-Yang - Dokumentasi Desain Frontend

## 🎨 Filosofi Desain

Tema Yin-Yang yang diterapkan pada frontend Domain.Nexa mencerminkan keseimbangan sempurna antara:
- **Yin (Gelap)** - Ketenangan, kedalaman, profesionalisme
- **Yang (Terang)** - Kejelasan, keterbukaan, kesederhanaan
- **Balance** - Harmoni antara keduanya

## ✨ Fitur Utama

### 1. **Sistem Warna Profesional**
- **Light Mode (Yang Dominant)**: Background putih bersih dengan aksen gelap
- **Dark Mode (Yin Dominant)**: Background gelap dengan aksen terang
- **Gradient Transitions**: Transisi halus antara Yin dan Yang
- **Semantic Colors**: Success, Warning, Error, Info dengan palet yang seimbang

### 2. **Tipografi Premium**
- **Heading**: Playfair Display (serif, elegan)
- **Body**: Inter (sans-serif, modern, mudah dibaca)
- **Font Weights**: 300-800 untuk variasi yang kaya

### 3. **Komponen UI Modern**

#### Login Page
- Animated gradient background
- Glassmorphism card effect
- Password visibility toggle
- Smooth form validation
- Loading states dengan spinner
- Decorative Yin-Yang circles

#### Dashboard
- Stat cards dengan gradient backgrounds
- Hover lift effects
- Progress bars animasi
- Quick action buttons
- Recent activity feed dengan avatars
- Chart placeholders

#### Header/Navigation
- Sticky header dengan backdrop blur
- Theme toggle dengan animasi
- User menu dropdown
- Notification badge
- Responsive mobile menu
- Yin-Yang logo branding

### 4. **Efek Visual**

#### Glassmorphism
```css
.glass-effect {
  backdrop-blur: 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}
```

#### Gradient Animations
```css
.animated-gradient {
  background: linear-gradient(-45deg, yin, balance, yang, balance);
  background-size: 400% 400%;
  animation: gradient 15s ease infinite;
}
```

#### Hover Effects
- Card lift on hover
- Scale transforms
- Color transitions
- Shadow enhancements

### 5. **Animasi Halus**

- **Fade In**: Elemen muncul dengan opacity
- **Slide In**: Elemen masuk dari samping
- **Hover Lift**: Card terangkat saat hover
- **Rotate**: Yin-Yang circle berputar
- **Gradient Flow**: Background gradient bergerak

### 6. **Responsive Design**

- Mobile-first approach
- Breakpoints: sm, md, lg, xl
- Flexible grid layouts
- Touch-friendly interactions
- Adaptive typography

## 🎯 Komponen yang Dibuat

### 1. **globals.css**
File CSS utama dengan:
- CSS Variables untuk theming
- Custom components (cards, buttons, inputs)
- Utility classes
- Animations
- Responsive utilities

### 2. **LoginPage.jsx**
Halaman login profesional dengan:
- Form validation
- Error handling
- Loading states
- Password toggle
- Responsive layout

### 3. **DashboardPage.jsx**
Dashboard modern dengan:
- Stat cards dengan icons
- Trend indicators
- Quick actions
- Activity feed
- Chart placeholders

### 4. **ThemeToggle.jsx**
Toggle tema dengan:
- Yin-Yang gradient background
- Smooth transitions
- LocalStorage persistence
- System preference detection

### 5. **Header.jsx**
Navigation header dengan:
- Sticky positioning
- User menu
- Notifications
- Mobile responsive
- Theme toggle integration

## 🚀 Cara Penggunaan

### Theme Toggle
```jsx
import ThemeToggle from '@/shared/components/ui/ThemeToggle';

<ThemeToggle />
```

### Custom Classes
```jsx
// Yin-Yang Cards
<div className="card-yin-yang">...</div>
<div className="card-yang-yin">...</div>
<div className="card-balanced">...</div>

// Buttons
<button className="btn-yin">Dark Button</button>
<button className="btn-yang">Light Button</button>
<button className="btn-balanced">Balanced Button</button>

// Text Gradients
<h1 className="text-gradient-yin-yang">Gradient Text</h1>

// Effects
<div className="glass-effect">Glassmorphism</div>
<div className="animated-gradient">Animated Background</div>
<div className="hover-lift">Lift on Hover</div>
```

## 🎨 Palet Warna

### Light Mode
- Background: `hsl(0, 0%, 100%)`
- Foreground: `hsl(240, 10%, 3.9%)`
- Primary: `hsl(240, 5.9%, 10%)`
- Secondary: `hsl(240, 4.8%, 95.9%)`

### Dark Mode
- Background: `hsl(240, 10%, 3.9%)`
- Foreground: `hsl(0, 0%, 98%)`
- Primary: `hsl(0, 0%, 98%)`
- Secondary: `hsl(240, 3.7%, 15.9%)`

### Semantic Colors
- Success: `hsl(142, 76%, 36%)`
- Warning: `hsl(38, 92%, 50%)`
- Destructive: `hsl(0, 72.2%, 50.6%)`
- Info: `hsl(199, 89%, 48%)`

## 📱 Responsive Breakpoints

- **sm**: 640px
- **md**: 768px
- **lg**: 1024px
- **xl**: 1280px
- **2xl**: 1536px

## ⚡ Performance

- CSS-in-JS dihindari untuk performa optimal
- Tailwind CSS untuk utility-first approach
- Lazy loading untuk komponen besar
- Optimized animations dengan GPU acceleration
- Minimal re-renders dengan React best practices

## 🔧 Customization

Untuk mengubah warna tema, edit CSS variables di `globals.css`:

```css
:root {
  --yin: 240 5.9% 10%;
  --yang: 0 0% 98%;
  --balance: 240 3.8% 46.1%;
}
```

## 📝 Best Practices

1. **Konsistensi**: Gunakan komponen yang sudah ada
2. **Accessibility**: Semua interaksi keyboard-friendly
3. **Performance**: Hindari animasi berlebihan
4. **Responsive**: Test di berbagai ukuran layar
5. **Theme**: Support light dan dark mode

## 🎉 Hasil Akhir

Frontend sekarang memiliki:
✅ Desain profesional dan modern
✅ Tema Yin-Yang yang seimbang
✅ Animasi halus dan menarik
✅ Responsive di semua device
✅ Dark/Light mode support
✅ Glassmorphism effects
✅ Professional typography
✅ Consistent design system

---

**Catatan**: Tema ini dirancang untuk memberikan pengalaman pengguna yang premium, profesional, dan menyenangkan dengan filosofi keseimbangan Yin-Yang.
