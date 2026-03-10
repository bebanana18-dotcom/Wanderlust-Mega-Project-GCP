# # ------------------- Stage 1: Build Stage ------------------------------
# FROM node:21 AS frontend-builder

# # Set the working directory to /app
# WORKDIR /app

# # Copy the package.json and package-lock.json for dependency installation
# COPY package*.json ./

# # Install dependencies
# RUN npm install

# # Copy the rest of the application code
# COPY . .

# # ------------------- Stage 2: Final Stage ------------------------------
# FROM node:21-slim

# # Set the working directory to /app
# WORKDIR /app

# # Copy built assets and dependencies from frontend-builder stage
# COPY --from=frontend-builder /app .

# # Copy the .env.sample file to .env.local
# COPY .env.docker .env.local

# # Expose port 5173 for the Node.js application
# EXPOSE 5173

# # Define the default command to run the application in development mode
# CMD ["npm", "run", "dev", "--", "--host"]




# ------------------- Stage 1: Build Stage ------------------------------
FROM node:21 AS frontend-builder

# Set the working directory to /app
WORKDIR /app

# Copy package files first for better layer caching
# (so Docker doesn't reinstall everything because you changed a comment)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Copy env file before build so Vite can bake in env vars at compile time
COPY .env.docker .env.local

# Build the app — output lands in /app/dist
# tsc lives in node_modules/.bin, so we make sure it's on PATH
ENV PATH /app/node_modules/.bin:$PATH
RUN npm run build

# ------------------- Stage 2: Final Stage ------------------------------
FROM nginx:alpine

# Ditch the default nginx placeholder page (it's not great)
RUN rm -rf /usr/share/nginx/html/*

# Copy the built static assets from stage 1
COPY --from=frontend-builder /app/dist /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 — nginx's natural habitat
EXPOSE 80

# nginx runs the show now
CMD ["nginx", "-g", "daemon off;"]