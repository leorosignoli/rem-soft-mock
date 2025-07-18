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
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
    };
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  size: number;
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
  };
}

export interface OrderUpdateEvent {
  orderId: number;
  eventType: 'CREATED' | 'UPDATED' | 'DELETED';
  timestamp: string;
}