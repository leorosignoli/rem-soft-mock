import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: '',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'orders',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'orders/:id',
    renderMode: RenderMode.Server
  }
];
