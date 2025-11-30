import React, { useState } from 'react';
import { Shield, Globe, Zap, Lock, Star, CheckCircle2, XCircle } from 'lucide-react';

const features = [
    {
        icon: Star,
        title: "Premium Namespace",
        description: "Exact Match Availability. Own your brand identity without compromise.",
        detail: "Don't settle for 'get-my-app.com'. Get the exact 4-letter domain you want.",
        weakness: "Competitors (.com) are saturated",
        color: "text-yellow-400",
        bg: "bg-yellow-400/10",
        border: "group-hover:border-yellow-400/50"
    },
    {
        icon: Globe,
        title: "Universal Scope",
        description: "One domain for Marketing, App, API, and Docs.",
        detail: "Stop fragmenting your identity across .app, .dev, and .io.",
        weakness: "Competitors (.app) restrict scope",
        color: "text-blue-400",
        bg: "bg-blue-400/10",
        border: "group-hover:border-blue-400/50"
    },
    {
        icon: Shield,
        title: "Sovereign Identity",
        description: "Future-proof generic TLD. Free from geopolitical risks.",
        detail: "Your infrastructure shouldn't depend on a specific territory's politics.",
        weakness: "Competitors (.io) have geopolitical risk",
        color: "text-emerald-400",
        bg: "bg-emerald-400/10",
        border: "group-hover:border-emerald-400/50"
    },
    {
        icon: Zap,
        title: "Future-First Prestige",
        description: "Signal innovation instantly. Part of a modern tech stack.",
        detail: "Position yourself as a next-gen enterprise from the very first URL click.",
        weakness: "Competitors (.net) feel outdated",
        color: "text-purple-400",
        bg: "bg-purple-400/10",
        border: "group-hover:border-purple-400/50"
    },
    {
        icon: Lock,
        title: "Flexible Security",
        description: "Enterprise security with full DNS control.",
        detail: "Strict SSL support without breaking local development or intranet setups.",
        weakness: "Competitors (.app) enforce rigid HSTS",
        color: "text-rose-400",
        bg: "bg-rose-400/10",
        border: "group-hover:border-rose-400/50"
    }
];

export const DomainAdvantages = () => {
    const [hoveredIndex, setHoveredIndex] = useState(null);

    return (
        <section className="relative py-20 overflow-hidden rounded-3xl my-8">
            {/* Background Elements */}
            <div className="absolute inset-0 bg-slate-950">
                <div className="absolute top-0 left-1/4 w-96 h-96 bg-blue-500/10 rounded-full blur-3xl" />
                <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-purple-500/10 rounded-full blur-3xl" />
            </div>

            <div className="relative container mx-auto px-6">
                {/* Header */}
                <div className="text-center max-w-3xl mx-auto mb-16">
                    <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-slate-900 border border-slate-800 mb-6">
                        <span className="flex h-2 w-2 rounded-full bg-blue-500 animate-pulse" />
                        <span className="text-sm font-medium text-slate-400">Strategic Analysis</span>
                    </div>
                    <h2 className="text-4xl md:text-5xl font-bold mb-6 bg-clip-text text-transparent bg-gradient-to-r from-white via-slate-200 to-slate-400">
                        Why We Chose <span className="text-blue-400">.nexa</span>
                    </h2>
                    <p className="text-lg text-slate-400 leading-relaxed">
                        We didn't just pick a domain. We chose a strategic asset that turns
                        competitor weaknesses into our unfair advantages.
                    </p>
                </div>

                {/* Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {features.map((feature, index) => {
                        const Icon = feature.icon;
                        const isHovered = hoveredIndex === index;

                        return (
                            <div
                                key={index}
                                onMouseEnter={() => setHoveredIndex(index)}
                                onMouseLeave={() => setHoveredIndex(null)}
                                className={`
                  group relative p-1 rounded-2xl transition-all duration-300
                  bg-gradient-to-b from-slate-800 to-slate-900
                  hover:from-slate-700 hover:to-slate-800
                  ${index === 0 ? 'lg:col-span-1 lg:row-span-2' : ''}
                `}
                            >
                                {/* Glow Effect on Hover */}
                                <div className={`absolute inset-0 rounded-2xl transition-opacity duration-500 opacity-0 group-hover:opacity-100 bg-gradient-to-r ${feature.bg.replace('/10', '/20')} blur-xl`} />

                                <div className="relative h-full bg-slate-950/90 backdrop-blur-sm rounded-xl p-6 border border-slate-800 group-hover:border-slate-700 transition-colors flex flex-col">
                                    {/* Icon Header */}
                                    <div className="flex items-start justify-between mb-6">
                                        <div className={`p-3 rounded-xl ${feature.bg} ${feature.color} ring-1 ring-inset ring-white/10`}>
                                            <Icon className="w-6 h-6" />
                                        </div>
                                        {isHovered && (
                                            <span className={`text-xs font-bold px-2 py-1 rounded-md ${feature.bg} ${feature.color} animate-fade-in`}>
                                                ADVANTAGE
                                            </span>
                                        )}
                                    </div>

                                    {/* Content */}
                                    <h3 className="text-xl font-bold text-white mb-2 group-hover:text-blue-200 transition-colors">
                                        {feature.title}
                                    </h3>
                                    <p className="text-slate-400 mb-4 leading-relaxed">
                                        {feature.description}
                                    </p>

                                    {/* Detail (Visible on large cards or hover) */}
                                    <p className={`text-sm text-slate-500 mb-6 transition-all duration-300 ${isHovered ? 'opacity-100' : 'opacity-70'}`}>
                                        {feature.detail}
                                    </p>

                                    {/* Comparison Footer */}
                                    <div className="mt-auto pt-4 border-t border-slate-800/50">
                                        <div className="flex items-center gap-2 text-xs text-rose-400/80 mb-1">
                                            <XCircle className="w-3 h-3" />
                                            <span>Problem: {feature.weakness}</span>
                                        </div>
                                        <div className={`flex items-center gap-2 text-xs ${feature.color} font-medium`}>
                                            <CheckCircle2 className="w-3 h-3" />
                                            <span>Solved by .nexa</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </section>
    );
};

export default DomainAdvantages;
