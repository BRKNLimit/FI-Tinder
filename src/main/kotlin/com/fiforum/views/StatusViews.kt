package com.fiforum.views

import com.fiforum.models.UserData
import kotlinx.html.*

fun HTML.waitingPage(name: String, initialWaitingUsers: List<UserData>, email: String) {
    layout(
        title = "Waiting // Matchmaker",
        headContent = {
            script {
                unsafe {
                    raw("""
                        const userEmail = '$email';
                        let particles = [];
                        let size = 350;
                        const centerX = size / 2;
                        const centerY = size / 2;
                        let angle = 0;

                        const companyColors = {
                            'Star Finanz': '#ff0000',
                            'Finanz Informatik': '#ffff00',
                            'inasys': '#00ff00',
                            'FI-TS': '#8000ff',
                            'FI-SP': '#0080ff',
                            'FINMAS': '#ff8000'
                        };
                        
                        const companyOffsets = {
                            'Star Finanz': { x: -60, y: -60 },
                            'Finanz Informatik': { x: 60, y: -60 },
                            'inasys': { x: 60, y: 60 },
                            'FI-TS': { x: -60, y: 60 },
                            'FI-SP': { x: 0, y: -80 },
                            'FINMAS': { x: 0, y: 80 }
                        };

                        function hashCode(str) {
                            let hash = 0;
                            for (let i = 0; i < str.length; i++) {
                                hash = ((hash << 5) - hash) + str.charCodeAt(i);
                                hash |= 0;
                            }
                            return Math.abs(hash);
                        }

                        async function refreshUsers() {
                            try {
                                console.log("Fetching waiting users...");
                                const response = await fetch('/api/waiting-users');
                                const users = await response.json();
                                console.log("Users received:", users.length);
                                document.getElementById('nodeCount').innerText = users.length;
                                
                                users.forEach(u => {
                                    if (!particles.find(p => p.email === u.email)) {
                                        console.log("Adding new particle for:", u.name);
                                        const seed = hashCode(u.name + u.email);
                                        const offset = companyOffsets[u.company] || { x: 0, y: 0 };
                                        
                                        particles.push({
                                            ...u,
                                            ox: offset.x + (seed % 60) - 30,
                                            oy: offset.y + ((seed / 60) % 60) - 30,
                                            radius: 3,
                                            color: companyColors[u.company] || '#ffffff'
                                        });
                                    }
                                });

                                for(let i=0; i<20; i++) {
                                    particles.forEach(p1 => {
                                        particles.forEach(p2 => {
                                            if(p1 === p2) return;
                                            const dx = p1.ox - p2.ox;
                                            const dy = p1.oy - p2.oy;
                                            const dist = Math.hypot(dx, dy) || 1;
                                            if(dist < 45) {
                                                const force = (45 - dist) / 4;
                                                p1.ox += (dx/dist) * force;
                                                p1.oy += (dy/dist) * force;
                                            }
                                        });
                                    });
                                }
                            } catch (e) { console.error("Refresh failed", e); }
                        }

                        function setupWebSocket() {
                            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                            const ws = new WebSocket(protocol + '//' + window.location.host + '/matching-ws');
                            ws.onmessage = (event) => { if (event.data === 'MATCHING_FINISHED') triggerGlitchReveal(); };
                            ws.onclose = () => { setTimeout(setupWebSocket, 2000); };
                        }

                        function triggerGlitchReveal() {
                            const overlay = document.getElementById('glitchOverlay');
                            const status = document.getElementById('glitchStatus');
                            overlay.style.display = 'flex';
                            let progress = 0;
                            const interval = setInterval(() => {
                                progress += Math.random() * 15;
                                if (progress >= 100) {
                                    progress = 100;
                                    clearInterval(interval);
                                    setTimeout(() => { window.location.href = '/myteam?email=' + userEmail; }, 500);
                                }
                                status.innerText = Math.floor(progress) + '%';
                            }, 100);
                        }

                        function animate() {
                            const canvas = document.getElementById('connectionCanvas');
                            if(!canvas) return;
                            const ctx = canvas.getContext('2d');
                            ctx.fillStyle = '#000';
                            ctx.fillRect(0, 0, size, size);
                            
                            angle += 0.0005;

                            const rotated = particles.map(p => {
                                const cos = Math.cos(angle);
                                const sin = Math.sin(angle);
                                return {
                                    ...p,
                                    x: centerX + (p.ox * cos - p.oy * sin),
                                    y: centerY + (p.ox * sin + p.oy * cos)
                                };
                            });

                            for (let i = 0; i < rotated.length; i++) {
                                for (let j = i + 1; j < rotated.length; j++) {
                                    const p1 = rotated[i];
                                    const p2 = rotated[j];
                                    const hasMatch = (p1.hobby === p2.hobby) || (p1.techInterest === p2.techInterest);

                                    if (hasMatch) {
                                        ctx.beginPath();
                                        ctx.moveTo(p1.x, p1.y);
                                        ctx.lineTo(p2.x, p2.y);
                                        ctx.strokeStyle = 'rgba(100, 100, 100, 0.15)';
                                        ctx.lineWidth = 0.5;
                                        ctx.stroke();
                                    }
                                }
                            }

                            rotated.forEach(p => {
                                ctx.beginPath();
                                ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
                                ctx.fillStyle = p.color;
                                ctx.fill();
                            });
                            
                            requestAnimationFrame(animate);
                        }

                        document.addEventListener('DOMContentLoaded', () => {
                            setupWebSocket();
                            refreshUsers();
                            setInterval(refreshUsers, 1000);
                            animate();
                        });
                    """)
                }
            }
        }
    ) {
        div("container") {
            h1 { +"Hallo, $name" }
            p { +"Verbindung zum Netzwerk hergestellt. Warte auf Matching-Signal..." }
            
            div("card") {
                style = "position: relative; width: 350px; height: 350px; margin: 20px auto; overflow: hidden; background: #000; border: 1px solid #333;"
                canvas {
                    id = "connectionCanvas"
                    width = "350"; height = "350"
                    style = "width: 100%; height: 100%;"
                }
                div {
                    style = "position: absolute; bottom: 10px; right: 10px; background: rgba(0,0,0,0.7); padding: 5px; font-size: 0.6rem; color: var(--accent); font-family: 'VT323';"
                    +"NODES_ACTIVE: " ; span { id = "nodeCount" ; +"0" }
                }
            }

            div {
                style = "margin-top: 20px;"
                span("spinner")
                span { +" Verbindung zum Server aktiv..." }
            }
        }
    }
}
