import { configureStore } from '@reduxjs/toolkit';

// Import reducers (placeholder - will be implemented)
// import authReducer from './slices/authSlice';
// import roomReducer from './slices/roomSlice';
// import gameReducer from './slices/gameSlice';
// import userReducer from './slices/userSlice';

export const store = configureStore({
  reducer: {
    // Auth state management
    // auth: authReducer,
    
    // Current room state
    // room: roomReducer,
    
    // Game state (rounds, bets, boosts)
    // game: gameReducer,
    
    // User profile and settings
    // user: userReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // Ignore these action types for WebSocket actions
        ignoredActions: ['websocket/connected', 'websocket/disconnected'],
        // Ignore these field paths in state
        ignoredPaths: ['ws.socket'],
      },
    }),
  devTools: import.meta.env.NODE_ENV !== 'production',
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
