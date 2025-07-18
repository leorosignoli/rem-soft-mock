export interface Order {
  id: number;
  orderDate: string;
  totalAmount: number;
  user: User;
  orderItems: OrderItem[];
}

export interface User {
  id: number;
  name: string;
  email: string;
}

export interface OrderItem {
  id: number;
  quantity: number;
  unitPrice: number;
  product: Product;
}

export interface Product {
  id: number;
  name: string;
  price: number;
  manufacturerName: string;
}

export interface PageRequest {
  page: number;
  size: number;
  sortBy: string;
  sortDirection: 'asc' | 'desc';
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface OrderUpdateEvent {
  orderId: number;
  eventType: 'CREATED' | 'UPDATED' | 'DELETED';
  timestamp: string;
}